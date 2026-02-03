import SwiftUI

struct SuggestionsView: View {
    @EnvironmentObject var authManager: AuthManager
    @Binding var selectedTab: Int
    
    @State private var suggestions: [Suggestion] = []
    @State private var isLoading = false
    @State private var errorMessage: String?
    
    var body: some View {
        NavigationView {
            ZStack {
                if isLoading {
                    ProgressView()
                } else if suggestions.isEmpty {
                    VStack(spacing: 16) {
                        Text("💪")
                            .font(.system(size: 60))
                        Text("No suggestions available")
                            .foregroundColor(.secondary)
                        Text("Update your activity preferences in your profile")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(suggestions) { suggestion in
                                SuggestionCard(suggestion: suggestion) {
                                    selectedTab = 1
                                }
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Suggestions")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Logout") {
                        authManager.logout()
                    }
                }
            }
            .task {
                await loadSuggestions()
            }
        }
    }
    
    private func loadSuggestions() async {
        guard let token = authManager.authToken else { return }
        
        isLoading = true
        
        do {
            suggestions = try await APIService.shared.getSuggestions(token: token)
            isLoading = false
        } catch {
            errorMessage = error.localizedDescription
            isLoading = false
        }
    }
}

struct SuggestionCard: View {
    let suggestion: Suggestion
    let onUse: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(suggestion.title)
                .font(.headline)
            
            HStack(spacing: 12) {
                Text(suggestion.type.capitalized)
                    .font(.caption)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(badgeColor(for: suggestion.type))
                    .foregroundColor(.white)
                    .cornerRadius(12)
                
                Text(suggestion.duration)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                Spacer()
            }
            
            Button(action: onUse) {
                Text("Use This")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 8)
                    .background(Color.green)
                    .cornerRadius(8)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
    
    private func badgeColor(for type: String) -> Color {
        switch type.lowercased() {
        case "individual": return Color.blue
        case "group": return Color.green
        case "gym": return Color.red
        case "sports": return Color.orange
        case "free": return Color.purple
        default: return Color.gray
        }
    }
}
