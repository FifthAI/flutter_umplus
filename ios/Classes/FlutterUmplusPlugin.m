#import "FlutterUmplusPlugin.h"
#import <UMCommon/MobClick.h>
#import <UMCommon/UMCommon.h>
#import <UMCommonLog/UMCommonLogHeaders.h>
#import <UMErrorCatch/UMErrorCatch.h>

@implementation FlutterUmplusPlugin {
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar> *)registrar {
  FlutterMethodChannel *channel =
      [FlutterMethodChannel methodChannelWithName:@"ygmpkk/flutter_umplus"
                                  binaryMessenger:[registrar messenger]];
  FlutterUmplusPlugin *instance = [[FlutterUmplusPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall *)call
                  result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS "
        stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"init" isEqualToString:call.method]) {
    [self initSetup:call result:result];
  } else if ([@"loginOnAction" isEqualToString:call.method]) {
    [self loginOnAction:call result:result];
  }else if ([@"loginOffAction" isEqualToString:call.method]) {
      [self loginOffAction:call result:result];
    } else if ([@"beginPageView" isEqualToString:call.method]) {
    [self beginPageView:call result:result];
  } else if ([@"endPageView" isEqualToString:call.method]) {
    [self endPageView:call result:result];
  } else if ([@"event" isEqualToString:call.method]) {
    [self event:call result:result];
    result(nil);
  } else {
    result(FlutterMethodNotImplemented);
  }
}


- (void)initSetup:(FlutterMethodCall *)call result:(FlutterResult)result {
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"Info" ofType:@"plist"];
    NSMutableDictionary *infoDict = [NSMutableDictionary dictionaryWithContentsOfFile:bundlePath];
    NSString *appKey = [infoDict objectForKey:@"umAppKey"];
    NSString *channel = [infoDict objectForKey:@"MARKET_CHANNEL_VALUE"];
    
    
    NSLog(@"集成测试的deviceID-initSetup-appKey:%@", appKey);
    NSLog(@"集成测试的deviceID-initSetup-channel:%@", channel);


    
  BOOL logEnable = [call.arguments[@"logEnable"] boolValue];
  BOOL reportCrash = [call.arguments[@"reportCrash"] boolValue];

    
  [UMCommonLogManager setUpUMCommonLogManager];
    
  NSString *deviceID = [UMConfigure deviceIDForIntegration];
  NSLog(@"集成测试的deviceID:%@", deviceID);

  [UMConfigure initWithAppkey:appKey channel:channel];

  [UMConfigure setLogEnabled:logEnable];

  [MobClick setCrashReportEnabled:reportCrash];

  [UMErrorCatch initErrorCatch];
    
  result(nil);
}

- (void)beginPageView:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];

  NSLog(@"beginPageView: %@", name);

  [MobClick beginLogPageView:name];

  result(nil);
}

- (void)endPageView:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];

  NSLog(@"endPageView: %@", name);

  [MobClick endLogPageView:name];
  result(nil);
}

- (void)loginOnAction:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *userId = call.arguments[@"userId"];

  NSLog(@"loginOnAction: %@", userId);

  [MobClick profileSignInWithPUID:userId ];

  result(nil);
}

- (void)loginOffAction:(FlutterMethodCall *)call result:(FlutterResult)result {

  NSLog(@"profileSignOff");

  [MobClick profileSignOff];

  result(nil);
}

- (void)event:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];
  NSString *label = call.arguments[@"label"];

  NSLog(@"event name: %@", name);
  NSLog(@"event label: %@", name);

  // TODO add attributes

  [MobClick event:name label:label];

  result(nil);
}

@end
