import UIKit
import AEPCore
import AEPEdgeConsent
import AEPAssurance
import AEPEdgeIdentity
import AEPEdge
import AEPUserProfile
import AEPIdentity
import AEPLifecycle
import AEPSignal
import AEPServices
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    MobileCore.setLogLevel(.debug) //Para hacer debuger
    let appState = application.applicationState
    let extensions = [
                     Consent.self,
                     Assurance.self,
                     AEPEdgeIdentity.Identity.self,
                     AEPIdentity.Identity.self,
                     Edge.self,
                     UserProfile.self,
                     Lifecycle.self,
                     Signal.self
                   ]
    MobileCore.registerExtensions(extensions, {
           MobileCore.configureWith(appId: "0378c3e801fb/8a76ce67efee/launch-cc6c25ca7765-development")
           if appState != .background {
               MobileCore.lifecycleStart(additionalContextData: ["contextDataKey": "contextDataVal"])
           }
    })
    MobileCore.setWrapperType(.flutter)
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    
  }
}
