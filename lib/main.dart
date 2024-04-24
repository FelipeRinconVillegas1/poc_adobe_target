import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_aepcore/flutter_aepcore.dart';
import 'package:flutter_aepassurance/flutter_aepassurance.dart';

void main() {
  runApp(const MainApp());
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  static const platform = MethodChannel('omni.pro/optimizer');

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              InkWell(
                onTap: () async {
                  final Event event = Event({
                    "eventName": "testEventName",
                    "eventType": "testEventType",
                    "eventSource": "testEventSource",
                    "eventData": {"eventDataKey": "eventDataValue"}
                  });
                  try {
                    await MobileCore.dispatchEvent(event);
                  } on PlatformException catch (e) {
                    log("Failed to dispatch event '${e.message}''");
                  }
                },
                child: const Text('PRUEBA DEL PLUGIN FLUTTER AEP CORE'),
              ),
              InkWell(
                onTap: () async {
                  String version = await Assurance.extensionVersion;
                  log("Assurance extension version: $version");
                  Assurance.startSession(
                      "https://omni_flutter.com/?adb_validation_sessionid=266bb5d0-7552-46d1-a5d8-e4d1130711fb");
                },
                child: const Text('Init Assurance'),
              ),
              InkWell(
                onTap: () async {
                  try {
                    final String result = await platform.invokeMethod('health');
                    log("Health: $result");
                  } on PlatformException catch (e) {
                    log("Failed to get battery level: '${e.message}'.");
                  }
                },
                child: const Text('Health Check, method channel'),
              ),
              InkWell(
                onTap: () async {
                  try {
                    final String result = await platform.invokeMethod('getPropositions');
                    log("LOG_AEP: $result");
                  } on PlatformException catch (e) {
                    log("LOG_AEP: '${e.message}'.");
                  }
                },
                child: const Text('Get propositions, method channel'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
