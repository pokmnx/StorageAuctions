//
//  AuctionListCell.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/28/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AuctionListCell : UITableViewCell


@property (weak, nonatomic) IBOutlet UIImageView *auctionImageView;
@property (weak, nonatomic) IBOutlet UILabel *auctionViews;
@property (weak, nonatomic) IBOutlet UILabel *auctionBids;
@property (weak, nonatomic) IBOutlet UILabel *auctionTitle;
@property (weak, nonatomic) IBOutlet UILabel *auctionAddress;
@property (weak, nonatomic) IBOutlet UILabel *auctionStatus;
@property (weak, nonatomic) IBOutlet UILabel *currentBidPrice;
@property (weak, nonatomic) IBOutlet UILabel *time;


@end
