import SwiftUI

@main
struct BountyHunterApp: App {
    @StateObject private var authManager = AuthManager.shared
    
    var body: some Scene {
        WindowGroup {
            if authManager.isAuthenticated {
                DashboardView()
            } else {
                LandingView()
            }
        }
    }
}
