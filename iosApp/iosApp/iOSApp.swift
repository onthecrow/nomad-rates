import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        AppInitializer.shared.initialize(platform: IOSPlatform())
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
