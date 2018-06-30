//
//  AuctionRequest.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/27/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuctionRequest : NSObject

@property(nonatomic) NSString* amount_owed;
@property(nonatomic) NSString* alt_unit_number;
@property(nonatomic) NSString* reserve;
@property(nonatomic) NSString* time_end;
@property(nonatomic) NSString* terms;
@property(nonatomic) NSString* prebid_close;
@property(nonatomic) NSString* lock_tag;
@property(nonatomic) NSString* fees;
@property(nonatomic) NSString* batch_email;
@property(nonatomic) NSString* cleanout_other;
@property(nonatomic) NSString* payment;
@property(nonatomic) NSString* time_st_zone;
@property(nonatomic) NSString* cleanout;
@property(nonatomic) NSString* access;
@property(nonatomic) NSString* auction_type;
@property(nonatomic) NSString* time_start;
@property(nonatomic) NSString* facility_id;
@property(nonatomic) NSString* unit_size;
@property(nonatomic) NSString* descr;
@property(nonatomic) NSString* unit_number;
@property(nonatomic) NSString* time_end_zone;
@property(nonatomic) NSString* save_terms;
@property(nonatomic) NSString* payment_other;
@property(nonatomic) NSString* tenant_name;

@end
