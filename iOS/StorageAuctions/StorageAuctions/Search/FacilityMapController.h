//
//  FacilityMapController.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "LMGeocoder.h"

@interface FacilityMapController : UIViewController

@property (nonatomic) NSString* address;
@property (nonatomic) LMAddress* mapAddress;

@end
