//
//  ServiceManager.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/18/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#ifndef ServiceManager_h
#define ServiceManager_h


#endif /* ServiceManager_h */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "LMGeocoder.h"
#import "UIViewController+LGSideMenuController.h"
#import "Facility.h"
#import "AuctionRequest.h"
#import "Auction.h"


@interface ServiceManager : NSObject

@property (nonatomic) Boolean bLoginFirstTime;
@property (nonatomic) LGSideMenuController* mainMenuController;
@property (nonatomic) NSMutableArray* auctionArr;


+(ServiceManager*) sharedManager;

-(void) loginWith:(NSString*) username password:(NSString*) password completion:(void (^)(BOOL bSuccess, NSString* error)) completion;
-(void) signupWith:(NSString*) email password:(NSString*) password username:(NSString*) username phone:(NSString*) phone name:(NSString*) name role:(NSString*) role completion:(void (^)(BOOL bSuccess, NSString* error)) completion;
- (void) addNewFacilityWith:(NSString*) name email:(NSString*) email website:(NSString*) website phone:(NSString*) phone taxRate:(CGFloat) taxRate commissionRate:(CGFloat) commissionRate terms:(NSString*) terms address:(LMAddress*) address completion:(void (^)(BOOL bSuccess, NSString* error)) completion;
- (void) getAllFacilities:(void (^)(NSArray* facArr)) completion;
- (void) createAuction:(AuctionRequest*) request completion:(void (^)(BOOL bSuccess, Auction* auction, NSString* error)) completion;
- (void) getAllAuctions:(void (^)(NSArray* auctArr)) completion;
-(void) setImage:(NSInteger) auctionID image:(NSData*) imageData completion:(void (^)(BOOL bSuccess, NSString* mediaID, NSString* error)) completion;
-(void) deleteImage:(NSInteger) auctionID mediaID:(NSString*) mediaID completion:(void (^)(BOOL bSuccess, NSString* error)) completion;

@end
