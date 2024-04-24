package com.example.test_target

import android.os.Bundle
import android.util.Log
import com.adobe.marketing.mobile.Assurance
import androidx.core.os.UserManagerCompat
import com.adobe.marketing.mobile.AdobeCallbackWithError
import com.adobe.marketing.mobile.AdobeError
import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.edge.identity.Identity;
import com.adobe.marketing.mobile.Extension
import com.adobe.marketing.mobile.Lifecycle
import com.adobe.marketing.mobile.LoggingMode
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.UserProfile
import com.adobe.marketing.mobile.WrapperType
import com.adobe.marketing.mobile.edge.identity.AuthenticatedState
import com.adobe.marketing.mobile.edge.identity.IdentityItem
import com.adobe.marketing.mobile.edge.identity.IdentityMap
import com.adobe.marketing.mobile.optimize.DecisionScope
import com.adobe.marketing.mobile.optimize.Optimize
import com.adobe.marketing.mobile.optimize.OptimizeProposition
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.Objects
import java.util.concurrent.TimeUnit

class MainActivity : FlutterActivity() {

    private val channel = "omni.pro/optimizer"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            channel
        ).setMethodCallHandler { call, result ->
            if (call.method == "health") {
                val health = health()
                result.success(health)
            } else if (call.method == "getPropositions") {
                val propositions = getPropositions()
                result.success(propositions)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun health(): String {
        return "Health 200"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UserManagerCompat.isUserUnlocked(applicationContext)) {
            MobileCore.setApplication(application)
            MobileCore.setLogLevel(LoggingMode.DEBUG)
            MobileCore.configureWithAppID("")

            val extensions = listOf(
                Assurance.EXTENSION,
                Identity.EXTENSION,
                Edge.EXTENSION,
                Identity.EXTENSION,
                Optimize.EXTENSION,
                com.adobe.marketing.mobile.Identity.EXTENSION,
                // Edge::class.java,
                UserProfile.EXTENSION,
                Lifecycle.EXTENSION
            )

            MobileCore.registerExtensions(extensions as MutableList<Class<out Extension>>) {
                Log.d("LOG_AEP", "AEP Mobile SDK is initialized")
            }
            MobileCore.setWrapperType(WrapperType.FLUTTER);
        }
    }

    override fun onResume() {
        super.onResume()
        MobileCore.setApplication(application);
        MobileCore.lifecycleStart(null);
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause();
    }

    private fun getPropositions(): String {

        var result = ""



        Identity.getExperienceCloudId(
            object : AdobeCallbackWithError<String> {
                override fun fail(adobeError: AdobeError) {
                    Log.e(
                        "LOG_AEP_NATIVE_ERROR",
                        "Error: ${adobeError.errorName} - ${adobeError.errorCode}"
                    )
                }

                override fun call(experienceCloudIdRes: String?) {
                   /* Log.i("LOG_AEP_NATIVE", "Experience Cloud ID: $experienceCloudIdRes")
                    var experienceCloudId = experienceCloudIdRes ?: ""
                    Log.i("LOG_AEP_NATIVE", "Experience Cloud ID: $experienceCloudId")

                    val item = IdentityItem("felipe.rincon@omni.pro", AuthenticatedState.AUTHENTICATED, true)
                    val crmIdentityItem =
                        IdentityItem(experienceCloudId, AuthenticatedState.AUTHENTICATED, false)
                    val identityMap = IdentityMap()
                    identityMap.addItem(item, "Email")
                    identityMap.addItem(crmIdentityItem, "ECID")
                    Identity.updateIdentities(identityMap);
*/

                    val decisionScope = DecisionScope("mobile")

                    val decisionScopes = listOf(decisionScope)

                    Optimize.clearCachedPropositions()

                    Optimize.updatePropositions(decisionScopes,null,null)

                    Optimize.getPropositions(decisionScopes, object :
                        AdobeCallbackWithError<Map<DecisionScope, OptimizeProposition>> {
                        override fun fail(adobeError: AdobeError) {
                            Log.e(
                                "LOG_AEP_NATIVE_ERROR",
                                "Error: ${adobeError.errorName} - ${adobeError.errorCode}"
                            )
                        }

                        override fun call(propositionsMap: Map<DecisionScope, OptimizeProposition>?) {
                            propositionsMap?.let { map ->
                                Log.i("LOG_AEP_NATIVE", "Propositions: ${map.keys.size}")
                                if (map.isNotEmpty()) {
                                    for ((scope, proposition) in map) {
                                        // Handle propositions
                                        result = proposition.toString()
                                    }
                                }
                            }
                        }
                    })
                }
            }
        )


        return result
    }
}
