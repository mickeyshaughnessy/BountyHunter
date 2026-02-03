import SwiftUI

struct RegisterView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject var authManager: AuthManager
    
    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @State private var fullName = ""
    @State private var location = ""
    @State private var age = ""
    @State private var weight = ""
    @State private var paymentInfo = ""
    @State private var selectedActivities: Set<ActivityType> = []
    
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var showLogin = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                Text("Create Your Account")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 24)
                
                VStack(spacing: 16) {
                    FormField(title: "Username *", text: $username)
                    FormField(title: "Email *", text: $email, keyboardType: .emailAddress)
                    FormSecureField(title: "Password *", text: $password)
                    FormField(title: "Full Name", text: $fullName)
                    
                    HStack(spacing: 12) {
                        FormField(title: "Location", text: $location)
                        FormField(title: "Age", text: $age, keyboardType: .numberPad)
                    }
                    
                    FormField(title: "Weight (lbs)", text: $weight, keyboardType: .numberPad)
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Activity Preferences")
                            .font(.subheadline)
                            .fontWeight(.medium)
                        
                        FlowLayout(spacing: 8) {
                            ForEach(ActivityType.allCases) { type in
                                ActivityChip(
                                    type: type,
                                    isSelected: selectedActivities.contains(type)
                                ) {
                                    if selectedActivities.contains(type) {
                                        selectedActivities.remove(type)
                                    } else {
                                        selectedActivities.insert(type)
                                    }
                                }
                            }
                        }
                    }
                    
                    FormField(title: "Payment Info", text: $paymentInfo)
                }
                
                if let error = errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                        .padding(.horizontal)
                }
                
                Button(action: register) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("Create Account")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(isLoading)
                
                NavigationLink(destination: LoginView(), isActive: $showLogin) {
                    Button(action: { showLogin = true }) {
                        Text("Already have an account?")
                            .foregroundColor(.accentColor)
                    }
                }
                .padding(.bottom, 24)
            }
            .padding(.horizontal, 24)
        }
        .background(Color(.systemGroupedBackground))
        .navigationBarTitleDisplayMode(.inline)
    }
    
    private func register() {
        guard !username.isEmpty, !email.isEmpty, !password.isEmpty else {
            errorMessage = "Please fill required fields"
            return
        }
        
        isLoading = true
        errorMessage = nil
        
        let request = RegisterRequest(
            username: username,
            email: email,
            password: password,
            fullName: fullName.isEmpty ? nil : fullName,
            location: location.isEmpty ? nil : location,
            age: Int(age),
            weight: Int(weight),
            activityTypes: selectedActivities.map { $0.rawValue },
            paymentInfo: paymentInfo.isEmpty ? nil : paymentInfo
        )
        
        Task {
            do {
                _ = try await APIService.shared.register(request: request)
                
                // Auto-login after registration
                let loginRequest = LoginRequest(username: username, password: password)
                let response = try await APIService.shared.login(request: loginRequest)
                
                await MainActor.run {
                    authManager.saveAuth(token: response.token, user: response.user)
                    dismiss()
                }
            } catch {
                await MainActor.run {
                    errorMessage = error.localizedDescription
                    isLoading = false
                }
            }
        }
    }
}

// MARK: - Form Components
struct FormField: View {
    let title: String
    @Binding var text: String
    var keyboardType: UIKeyboardType = .default
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.subheadline)
                .fontWeight(.medium)
            TextField(title, text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(keyboardType)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
        }
    }
}

struct FormSecureField: View {
    let title: String
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.subheadline)
                .fontWeight(.medium)
            SecureField(title, text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
        }
    }
}

struct ActivityChip: View {
    let type: ActivityType
    let isSelected: Bool
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            Text(type.displayName)
                .font(.subheadline)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.accentColor : Color(.systemGray5))
                .foregroundColor(isSelected ? .white : .primary)
                .cornerRadius(20)
        }
    }
}

// MARK: - Flow Layout
struct FlowLayout: Layout {
    var spacing: CGFloat = 8
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = FlowResult(
            in: proposal.replacingUnspecifiedDimensions().width,
            subviews: subviews,
            spacing: spacing
        )
        return result.size
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = FlowResult(
            in: bounds.width,
            subviews: subviews,
            spacing: spacing
        )
        for (index, subview) in subviews.enumerated() {
            subview.place(at: CGPoint(x: bounds.minX + result.frames[index].minX,
                                     y: bounds.minY + result.frames[index].minY),
                         proposal: ProposedViewSize(result.frames[index].size))
        }
    }
    
    struct FlowResult {
        var frames: [CGRect] = []
        var size: CGSize = .zero
        
        init(in maxWidth: CGFloat, subviews: Subviews, spacing: CGFloat) {
            var x: CGFloat = 0
            var y: CGFloat = 0
            var lineHeight: CGFloat = 0
            
            for subview in subviews {
                let size = subview.sizeThatFits(.unspecified)
                
                if x + size.width > maxWidth && x > 0 {
                    x = 0
                    y += lineHeight + spacing
                    lineHeight = 0
                }
                
                frames.append(CGRect(x: x, y: y, width: size.width, height: size.height))
                lineHeight = max(lineHeight, size.height)
                x += size.width + spacing
            }
            
            self.size = CGSize(width: maxWidth, height: y + lineHeight)
        }
    }
}
