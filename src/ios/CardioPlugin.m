//
//  MobileECTPlugin.h
//  OutSystems - Mobility Experts
//
//  Created by João Gonçalves on 25/11/15.

#import "CardioPlugin.h"

@interface CardioPlugin ()

typedef enum {
    ERROR_INVALID_ARGS = 0,
    ERROR_USER_CANCEL = 1,
    ERROR_CANT_READ_CARD = 2
} OSCardIOError;

@property (strong, nonatomic) NSString* callbackId;

@end

@implementation CardioPlugin

#pragma mark CardIO Cordova Plugin

- (void) scanCard:(CDVInvokedUrlCommand*) command {
    self.callbackId = command.callbackId;

    if ([CardIOUtilities canReadCardWithCamera]) {
        
        if([command.arguments count] == 0) {
            [self respondErrorTo:self.callbackId withErroCode:ERROR_INVALID_ARGS andErrorMessage:@"Invalid args."];
            return;
        }
        
        NSNumber* requireExpiry = [command.arguments objectAtIndex:0];
        NSNumber* requireCvv = [command.arguments objectAtIndex:1];
        NSNumber* requirePostalCode = [command.arguments objectAtIndex:2] ;
        
        CardIOPaymentViewController* paymentVC = [[CardIOPaymentViewController alloc] initWithPaymentDelegate:self];
        
        if(requireExpiry)
            paymentVC.collectExpiry = [requireExpiry boolValue];
        
        if(requireCvv)
            paymentVC.collectCVV = [requireCvv boolValue];
        
        if(requirePostalCode)
            paymentVC.collectPostalCode = [requirePostalCode boolValue];
        
        [self.viewController presentViewController:paymentVC animated:YES completion:nil];
        
    } else {
        
        [self respondErrorTo:self.callbackId withErroCode:ERROR_CANT_READ_CARD andErrorMessage:@"Can't scan with the camera."];
    }
}

#pragma mark Helper methods

- (void) respondSuccessTo:(NSString*)callbackId withData:(NSMutableDictionary*) dict {
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:&error];
    NSString *jsonString = @"";
    if (! jsonData) {
        NSLog(@"Got an error: %@", error);
    } else {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonString];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void) respondErrorTo:(NSString*)callbackId withErroCode:(int)code andErrorMessage: (NSString*) msg{
    NSMutableDictionary *obj = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
                                [NSNumber numberWithInt:code], @"error_code", msg, @"error_message", nil];

    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:obj
                                                       options:0
                                                         error:&error];
    NSString *jsonString = @"";
    if (! jsonData) {
        NSLog(@"Got an error: %@", error);
    } else {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:jsonString];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

#pragma mark CardIOPaymentViewControllerDelegate implementations

- (void)userDidCancelPaymentViewController:(CardIOPaymentViewController *)scanViewController {
    [scanViewController dismissViewControllerAnimated:YES completion:^{
        [self respondErrorTo:self.callbackId withErroCode:ERROR_USER_CANCEL andErrorMessage:@"Cancelled by user."];
    }];
}

- (void)userDidProvideCreditCardInfo:(CardIOCreditCardInfo *)info inPaymentViewController:(CardIOPaymentViewController *)scanViewController {
    
    [scanViewController dismissViewControllerAnimated:YES completion:^{
        NSMutableDictionary *jsonObj = [ [NSMutableDictionary alloc] initWithObjectsAndKeys :
                                        info.cardNumber, @"rawCardNumber",
                                        [CardIOCreditCardInfo displayStringForCardType:info.cardType
                                                                 usingLanguageOrLocale:scanViewController.languageOrLocale], @"cardType",
                                        info.redactedCardNumber, @"redactedCardNumber",
                                        nil
                                        ];
        
        if(info.expiryMonth > 0 && info.expiryYear > 0) {
            [jsonObj setObject:[NSNumber numberWithUnsignedInteger:info.expiryYear] forKey:@"expiryYear"];
            [jsonObj setObject:[NSNumber numberWithUnsignedInteger:info.expiryMonth] forKey:@"expiryMonth"];
        }
        
        if(info.cvv.length > 0) {
            [jsonObj setObject:info.cvv forKey:@"cvv"];
        } else {
            [jsonObj setObject:@"" forKey:@"cvv"];
        }
        
        if(info.postalCode.length > 0) {
            [jsonObj setObject:info.postalCode forKey:@"postalCode"];
        } else {
            [jsonObj setObject:@"" forKey:@"postalCode"];
        }
        
        [self respondSuccessTo:self.callbackId withData:jsonObj];
        
    }];
}

@end