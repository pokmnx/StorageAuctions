//
//  Facility.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface Facility : NSObject

@property (nonatomic) NSInteger fac_id;
@property (nonatomic) NSInteger user_id;
@property (nonatomic) NSInteger active;
@property (nonatomic) NSString* name;
@property (nonatomic) NSString* address;
@property (nonatomic) NSString* city;
@property (nonatomic) NSInteger acc;
@property (nonatomic) NSString* state_code;
@property (nonatomic) NSString* postal_code;
@property (nonatomic) NSString* country;
@property (nonatomic) CGFloat lat;
@property (nonatomic) CGFloat lon;
@property (nonatomic) NSString* phone;
@property (nonatomic) NSString* website;
@property (nonatomic) NSString* email;
@property (nonatomic) NSDate* time_created;
@property (nonatomic) NSDate* time_updated;
@property (nonatomic) NSString* source;
@property (nonatomic) NSInteger source_id;
@property (nonatomic) NSString* terms;
@property (nonatomic) CGFloat taxrate;
@property (nonatomic) NSInteger remote_id;
@property (nonatomic) CGFloat commission_rate;
@property (nonatomic) NSString* url;

@end
