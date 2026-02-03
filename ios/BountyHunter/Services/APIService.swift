import Foundation

class APIService {
    static let shared = APIService()
    
    private let baseURL = "http://localhost:5000/api"
    private let decoder: JSONDecoder = {
        let decoder = JSONDecoder()
        return decoder
    }()
    
    private let encoder: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        return encoder
    }()
    
    private init() {}
    
    // MARK: - Authentication
    func register(request: RegisterRequest) async throws -> User {
        let url = URL(string: "\(baseURL)/register")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.httpBody = try encoder.encode(request)
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode(User.self, from: data)
    }
    
    func login(request: LoginRequest) async throws -> LoginResponse {
        let url = URL(string: "\(baseURL)/login")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.httpBody = try encoder.encode(request)
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.invalidCredentials
        }
        
        return try decoder.decode(LoginResponse.self, from: data)
    }
    
    // MARK: - Suggestions
    func getSuggestions(token: String) async throws -> [Suggestion] {
        let url = URL(string: "\(baseURL)/suggestions")!
        var urlRequest = URLRequest(url: url)
        urlRequest.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode([Suggestion].self, from: data)
    }
    
    // MARK: - Workouts
    func logWorkout(token: String, request: LogWorkoutRequest) async throws -> Workout {
        let url = URL(string: "\(baseURL)/workouts")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        urlRequest.httpBody = try encoder.encode(request)
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode(Workout.self, from: data)
    }
    
    func getWorkoutHistory(token: String) async throws -> [Workout] {
        let url = URL(string: "\(baseURL)/workouts")!
        var urlRequest = URLRequest(url: url)
        urlRequest.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode([Workout].self, from: data)
    }
    
    // MARK: - Recurring Workouts
    func getRecurringWorkouts(token: String) async throws -> [RecurringWorkout] {
        let url = URL(string: "\(baseURL)/recurring")!
        var urlRequest = URLRequest(url: url)
        urlRequest.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode([RecurringWorkout].self, from: data)
    }
    
    func createRecurringWorkout(token: String, request: CreateRecurringRequest) async throws -> RecurringWorkout {
        let url = URL(string: "\(baseURL)/recurring")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        urlRequest.httpBody = try encoder.encode(request)
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw APIError.serverError
        }
        
        return try decoder.decode(RecurringWorkout.self, from: data)
    }
}

// MARK: - API Errors
enum APIError: LocalizedError {
    case invalidCredentials
    case serverError
    case networkError
    
    var errorDescription: String? {
        switch self {
        case .invalidCredentials:
            return "Invalid username or password"
        case .serverError:
            return "Server error occurred"
        case .networkError:
            return "Network connection failed"
        }
    }
}
