import SwiftUI

struct LogWorkoutView: View {
    @EnvironmentObject var authManager: AuthManager
    
    @State private var title = ""
    @State private var type = "individual"
    @State private var duration = ""
    @State private var notes = ""
    @State private var isLoading = false
    @State private var showSuccess = false
    
    let workoutTypes = ["individual", "group", "gym", "sports", "free"]
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    VStack(alignment: .leading, spacing: 16) {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Workout Title *")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            TextField("Morning Run", text: $title)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                        }
                        
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Type *")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Picker("Type", selection: $type) {
                                ForEach(workoutTypes, id: \.self) { type in
                                    Text(type.capitalized).tag(type)
                                }
                            }
                            .pickerStyle(MenuPickerStyle())
                            .padding(8)
                            .background(Color(.systemGray6))
                            .cornerRadius(8)
                        }
                        
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Duration *")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            TextField("30 mins", text: $duration)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                        }
                        
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Notes")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            TextEditor(text: $notes)
                                .frame(height: 100)
                                .padding(4)
                                .background(Color(.systemGray6))
                                .cornerRadius(8)
                        }
                    }
                    .padding()
                    .background(Color(.systemBackground))
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
                    
                    Button(action: saveWorkout) {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Save Workout")
                                .fontWeight(.semibold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                    .disabled(isLoading)
                }
                .padding()
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Log Workout")
            .alert("Success", isPresented: $showSuccess) {
                Button("OK", role: .cancel) { }
            } message: {
                Text("Workout logged successfully!")
            }
        }
    }
    
    func fillFromSuggestion(suggestion: Suggestion) {
        title = suggestion.title
        type = suggestion.type
        duration = suggestion.duration
    }
    
    private func saveWorkout() {
        guard let token = authManager.authToken,
              !title.isEmpty, !duration.isEmpty else {
            return
        }
        
        isLoading = true
        
        let request = LogWorkoutRequest(
            title: title,
            type: type,
            duration: duration,
            data: WorkoutData(notes: notes.isEmpty ? nil : notes)
        )
        
        Task {
            do {
                _ = try await APIService.shared.logWorkout(token: token, request: request)
                
                await MainActor.run {
                    title = ""
                    duration = ""
                    notes = ""
                    type = "individual"
                    isLoading = false
                    showSuccess = true
                }
            } catch {
                await MainActor.run {
                    isLoading = false
                }
            }
        }
    }
}
