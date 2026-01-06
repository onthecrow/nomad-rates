import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        Platform_iosKt.doInitFirebase()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
