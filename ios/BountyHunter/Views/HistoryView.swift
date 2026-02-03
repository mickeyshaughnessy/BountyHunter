import SwiftUI

struct HistoryView: View {
    @EnvironmentObject var authManager: AuthManager
    
    @State private var workouts: [Workout] = []
    @State private var isLoading = false
    
    var body: some View {
        NavigationView {
            ZStack {
                if isLoading {
                    ProgressView()
                } else if workouts.isEmpty {
                    VStack(spacing: 16) {
                        Text("📊")
                            .font(.system(size: 60))
                        Text("No workouts logged yet")
                            .foregroundColor(.secondary)
                        Text("Start tracking your fitness journey!")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(workouts.reversed()) { workout in
                                WorkoutCard(workout: workout)
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("History")
            .task {
                await loadHistory()
            }
            .refreshable {
                await loadHistory()
            }
        }
    }
    
    private func loadHistory() async {
        guard let token = authManager.authToken else { return }
        
        isLoading = true
        
        do {
            workouts = try await APIService.shared.getWorkoutHistory(token: token)
            isLoading = false
        } catch {
            isLoading = false
        }
    }
}

struct WorkoutCard: View {
    let workout: Workout
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(formattedDate)
                .font(.caption)
                .foregroundColor(.secondary)
            
            Text(workout.title)
                .font(.headline)
            
            HStack(spacing: 12) {
                Text(workout.type.capitalized)
                    .font(.caption)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(badgeColor(for: workout.type))
                    .foregroundColor(.white)
                    .cornerRadius(12)
                
                Text(workout.duration)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            if let notes = workout.data?.notes, !notes.isEmpty {
                Text(notes)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .padding(.top, 4)
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
    
    private var formattedDate: String {
        let isoFormatter = ISO8601DateFormatter()
        isoFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        
        if let date = isoFormatter.date(from: workout.timestamp) {
            let formatter = DateFormatter()
            formatter.dateStyle = .medium
            return formatter.string(from: date)
        }
        
        return workout.timestamp.components(separatedBy: "T").first ?? workout.timestamp
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
