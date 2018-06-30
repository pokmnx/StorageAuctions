//
//  Auction.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/28/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuctionMeta.h"


@interface Auction : NSObject

@property (nonatomic) NSInteger auct_id;
@property (nonatomic) NSInteger facility_id;
@property (nonatomic) NSInteger auction_online_bid_id;
@property (nonatomic) NSInteger status;
@property (nonatomic) NSString* failed_reason;
@property (nonatomic) NSString* descr;
@property (nonatomic) NSDate* time_start;
@property (nonatomic) NSString* time_st_zone;
@property (nonatomic) NSDate* time_end;
@property (nonatomic) NSString* time_end_zone;
@property (nonatomic) NSString* amount_owed;
@property (nonatomic) float fees;
@property (nonatomic) NSString* tenant_name;
@property (nonatomic) NSInteger unit_number;
@property (nonatomic) NSInteger unit_size;
@property (nonatomic) NSString* lock_tag;
@property (nonatomic) NSDate* time_created;
@property (nonatomic) NSDate* time_updated;
@property (nonatomic) NSInteger bid_count;
@property (nonatomic) NSInteger winner_code;
@property (nonatomic) float seller_fee;
@property (nonatomic) float buyer_fee;
@property (nonatomic) NSInteger emailed;
@property (nonatomic) NSString* terms;
@property (nonatomic) NSInteger queue_flag;
@property (nonatomic) NSString* auction_id;
@property (nonatomic) AuctionMeta* meta;
@property (nonatomic) float tax;
@property (nonatomic) NSString* remote_id;
@property (nonatomic) NSString* source_id;
@property (nonatomic) float reserve;
@property (nonatomic) NSInteger bid_schedule_id;
@property (nonatomic) NSInteger alt_unit_number;
@property (nonatomic) NSInteger batch_email;
@property (nonatomic) NSString* title;
@property (nonatomic) NSString* facility_name;
@property (nonatomic) NSString* address;
@property (nonatomic) NSString* city;
@property (nonatomic) NSString* state_code;
@property (nonatomic) NSInteger user_id;
@property (nonatomic) float amount;
@property (nonatomic) NSInteger bid_user_id;
@property (nonatomic) NSInteger media_auction_id;
@property (nonatomic) NSString* photo_path_size0;
@property (nonatomic) NSString* photo_path_size1;
@property (nonatomic) NSString* photo_path_size2;
@property (nonatomic) NSString* photo_path_size3;
@property (nonatomic) NSInteger view_count;
@property (nonatomic) NSString* os_date;
@property (nonatomic) NSString* os_time;
@property (nonatomic) NSString* url;
@property (nonatomic) NSString* group;
@property (nonatomic) NSString* type;
@property (nonatomic) NSString* category;

@end
