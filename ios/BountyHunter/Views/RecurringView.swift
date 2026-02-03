import SwiftUI

struct RecurringView: View {
    @EnvironmentObject var authManager: AuthManager
    
    @State private var recurring: [RecurringWorkout] = []
    @State private var isLoading = false
    @State private var showCreateDialog = false
    
    var body: some View {
        NavigationView {
            ZStack {
                if isLoading {
                    ProgressView()
                } else if recurring.isEmpty {
                    VStack(spacing: 16) {
                        Text("🔄")
                            .font(.system(size: 60))
                        Text("No recurring workouts set up")
                            .foregroundColor(.secondary)
                        Text("Create one to stay consistent!")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(recurring) { item in
                                RecurringCard(recurring: item)
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Recurring")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showCreateDialog = true }) {
                        Image(systemName: "plus.circle.fill")
                            .foregroundColor(.green)
                    }
                }
            }
            .sheet(isPresented: $showCreateDialog) {
                CreateRecurringView(onCreated: {
                    Task {
                        await loadRecurring()
                    }
                })
            }
            .task {
                await loadRecurring()
            }
        }
    }
    
    private func loadRecurring() async {
        guard let token = authManager.authToken else { return }
        
        isLoading = true
        
        do {
            recurring = try await APIService.shared.getRecurringWorkouts(token: token)
            isLoading = false
        } catch {
            isLoading = false
        }
    }
}

struct RecurringCard: View {
    let recurring: RecurringWorkout
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(recurring.title)
                .font(.headline)
            
            HStack {
                Image(systemName: "calendar")
                    .foregroundColor(.accentColor)
                Text(recurring.schedule.capitalized)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color(.systemGray6))
            .cornerRadius(8)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

struct CreateRecurringView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject var authManager: AuthManager
    
    @State private var title = ""
    @State private var schedule = "daily"
    @State private var isLoading = false
    
    let schedules = ["daily", "weekly", "biweekly", "monthly"]
    var onCreated: () -> Void
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("Workout Title", text: $title)
                    
                    Picker("Schedule", selection: $schedule) {
                        ForEach(schedules, id: \.self) { schedule in
                            Text(schedule.capitalized).tag(schedule)
                        }
                    }
                }
                
                Section {
                    Button(action: create) {
                        if isLoading {
                            HStack {
                                Spacer()
                                ProgressView()
                                Spacer()
                            }
                        } else {
                            Text("Create")
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .disabled(title.isEmpty || isLoading)
                }
            }
            .navigationTitle("New Recurring Workout")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func create() {
        guard let token = authManager.authToken, !title.isEmpty else {
            return
        }
        
        isLoading = true
        
        let request = CreateRecurringRequest(title: title, schedule: schedule)
        
        Task {
            do {
                _ = try await APIService.shared.createRecurringWorkout(token: token, request: request)
                
                await MainActor.run {
                    dismiss()
                    onCreated()
                }
            } catch {
                await MainActor.run {
                    isLoading = false
                }
            }
        }
    }
}
