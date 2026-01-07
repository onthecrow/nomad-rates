import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        Platform_iosKt.getPlatform().initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
