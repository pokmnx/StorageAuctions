//
//  AuctionMeta.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/28/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuctionMeta : NSObject

@property (nonatomic) NSInteger cleanout;
@property (nonatomic) NSString* cleanout_other;
@property (nonatomic) NSInteger payment;
@property (nonatomic) NSString* payment_other;
@property (nonatomic) NSInteger access;

@property (nonatomic) float currentBidPrice;
@property (nonatomic) NSArray* images;
@property (nonatomic) NSArray* imageIDs;

@end
