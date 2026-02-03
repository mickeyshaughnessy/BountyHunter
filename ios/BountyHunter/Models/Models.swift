import Foundation

// MARK: - User Models
struct User: Codable, Identifiable {
    let id: Int
    let username: String
    let email: String
}

struct LoginRequest: Codable {
    let username: String
    let password: String
}

struct LoginResponse: Codable {
    let token: String
    let user: User
}

struct RegisterRequest: Codable {
    let username: String
    let email: String
    let password: String
    let fullName: String?
    let location: String?
    let age: Int?
    let weight: Int?
    let activityTypes: [String]
    let paymentInfo: String?
    
    enum CodingKeys: String, CodingKey {
        case username, email, password, location, age, weight
        case fullName = "full_name"
        case activityTypes = "activity_types"
        case paymentInfo = "payment_info"
    }
}

// MARK: - Workout Models
struct Suggestion: Codable, Identifiable {
    let id: Int
    let title: String
    let type: String
    let duration: String
}

struct Workout: Codable, Identifiable {
    let id: Int
    let userId: Int
    let title: String
    let type: String
    let duration: String
    let data: WorkoutData?
    let timestamp: String
    
    enum CodingKeys: String, CodingKey {
        case id, title, type, duration, data, timestamp
        case userId = "user_id"
    }
}

struct WorkoutData: Codable {
    let notes: String?
}

struct LogWorkoutRequest: Codable {
    let title: String
    let type: String
    let duration: String
    let data: WorkoutData
}

// MARK: - Recurring Models
struct RecurringWorkout: Codable, Identifiable {
    let id: Int
    let userId: Int
    let title: String
    let schedule: String
    let createdAt: String
    
    enum CodingKeys: String, CodingKey {
        case id, title, schedule
        case userId = "user_id"
        case createdAt = "created_at"
    }
}

struct CreateRecurringRequest: Codable {
    let title: String
    let schedule: String
}

// MARK: - Activity Type
enum ActivityType: String, CaseIterable, Identifiable {
    case individual = "individual"
    case group = "group"
    case gym = "gym"
    case sports = "sports"
    case free = "free"
    
    var id: String { rawValue }
    
    var displayName: String {
        rawValue.capitalized
    }
}
