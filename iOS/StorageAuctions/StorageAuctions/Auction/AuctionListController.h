//
//  AuctionListController.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/28/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum {
    AF_ALL,
    AF_ACTIVE,
    AF_UPCOMING,
    AF_CANCELLED
} AUCTION_FILTER;

@interface AuctionListController : UIViewController

@property(nonatomic) AUCTION_FILTER filter;
@property(nonatomic) NSMutableArray* auctionArr;

@property (weak, nonatomic) IBOutlet UITableView *auctionListView;




@end
