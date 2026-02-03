import SwiftUI

struct DashboardView: View {
    @EnvironmentObject var authManager: AuthManager
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            SuggestionsView(selectedTab: $selectedTab)
                .tabItem {
                    Label("Suggestions", systemImage: "lightbulb")
                }
                .tag(0)
            
            LogWorkoutView()
                .tabItem {
                    Label("Log", systemImage: "plus.circle")
                }
                .tag(1)
            
            HistoryView()
                .tabItem {
                    Label("History", systemImage: "clock")
                }
                .tag(2)
            
            RecurringView()
                .tabItem {
                    Label("Recurring", systemImage: "repeat")
                }
                .tag(3)
        }
        .accentColor(.accentColor)
    }
}
