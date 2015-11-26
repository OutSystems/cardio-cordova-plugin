//
//  MobileECTPlugin.h
//  OutSystems - Mobility Experts
//
//  Created by João Gonçalves on 25/11/15.

#import <Cordova/CDVPlugin.h>
#import "CardIO.h"

@interface CardioPlugin : CDVPlugin <CardIOPaymentViewControllerDelegate>

- (void) scanCard:(CDVInvokedUrlCommand*) command;

@end