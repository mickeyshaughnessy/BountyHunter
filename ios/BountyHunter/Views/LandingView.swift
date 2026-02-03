import SwiftUI

struct LandingView: View {
    @State private var showLogin = false
    @State private var showRegister = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                Spacer()
                
                Text("🦅")
                    .font(.system(size: 80))
                
                Text("Track Your Fitness Journey")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                Text("Get personalized workout suggestions, track your progress, and stay consistent with your fitness goals.")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                
                Spacer()
                
                VStack(spacing: 12) {
                    NavigationLink(destination: RegisterView(), isActive: $showRegister) {
                        Button(action: { showRegister = true }) {
                            Text("Get Started")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.accentColor)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                        }
                    }
                    
                    NavigationLink(destination: LoginView(), isActive: $showLogin) {
                        Button(action: { showLogin = true }) {
                            Text("Sign In")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.clear)
                                .foregroundColor(.accentColor)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color.accentColor, lineWidth: 2)
                                )
                        }
                    }
                }
                .padding(.horizontal, 32)
                .padding(.bottom, 48)
            }
            .background(Color(.systemGroupedBackground))
        }
    }
}
