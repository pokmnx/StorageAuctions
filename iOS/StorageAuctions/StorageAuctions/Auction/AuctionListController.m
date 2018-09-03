//
//  AuctionListController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/28/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "AuctionListController.h"
#import "AuctionListCell.h"
#import "ServiceManager.h"
#import "Auction.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "NewAuctionController.h"
#import "MainTabBarController.h"
#import "AuctionDetailController.h"
#import "ProgressHUD.h"

@interface AuctionListController () <UITableViewDataSource, UITableViewDelegate>

@end

@implementation AuctionListController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.auctionArr ? self.auctionArr.count : 0;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    AuctionListCell* cell = (AuctionListCell*) [tableView dequeueReusableCellWithIdentifier:@"auctionListCell" forIndexPath:indexPath];
    Auction* auction = (Auction*)[self.auctionArr objectAtIndex:indexPath.row];
    
    cell.auctionViews.text = [NSString stringWithFormat:@"%ld", (long)auction.view_count];
    cell.auctionBids.text = [NSString stringWithFormat:@"%ld", (long)auction.bid_count];
    cell.auctionTitle.text = auction.title;
    cell.auctionAddress.text = auction.address;
    cell.time.text = [NSString stringWithFormat:@"%ldd Left", (long)[self getDifferenceDate:auction.time_end]];
    [cell.auctionImageView sd_setImageWithURL:[NSURL URLWithString:auction.photo_path_size1]];
    
    if (auction.meta != NULL && auction.meta.currentBidPrice != 0) {
        cell.currentBidPrice.text = [NSString stringWithFormat:@"$%.2f", auction.meta.currentBidPrice];
    } else {
        cell.currentBidPrice.text = @"$0";
    }
    
    switch (auction.status) {
        case 0:
            cell.auctionStatus.text = @"INACTIVE";
            break;
        case 1:
            cell.auctionStatus.text = @"ACTIVE";
            break;
        case 2:
            cell.auctionStatus.text = @"CLOSED";
            break;
        case 3:
            cell.auctionStatus.text = @"PAID";
            break;
        case 4:
            cell.auctionStatus.text = @"UNSOLD";
            break;
        case 5:
            cell.auctionStatus.text = @"FAILED PAYMENT";
            break;
        case 6:
            cell.auctionStatus.text = @"CANCELED";
            break;
        case 7:
            // cell.auctionStatus.text = @"CANCELED PAID";
            cell.auctionStatus.text = @"CANCELED";
            break;
        case 8:
            // cell.auctionStatus.text = @"CANCELLED FAILED";
            cell.auctionStatus.text = @"CANCELLED";
            break;
        case 9:
            cell.auctionStatus.text = @"FAILED FINAL";
            break;
        case 10:
            cell.auctionStatus.text = @"PENDING";
            break;
        case 11:
            cell.auctionStatus.text = @"ONSITE";
            break;
        case 88:
            cell.auctionStatus.text = @"UPCOMING";
            break;
        default:
            cell.auctionStatus.text = @"PROCESSING";
            break;
    }
    
    [cell.viewButton addTarget:self action:@selector(viewDetail:) forControlEvents:UIControlEventTouchUpInside];
    
    return cell;
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    Auction* auction = (Auction*)[self.auctionArr objectAtIndex:indexPath.row];
    [self viewAuctionDetail:auction];
}

- (void) viewDetail:(id) sender {
    UITableViewCell* cell = (UITableViewCell*)[[((UIButton*)sender) superview] superview];
    NSIndexPath* indexPath = [self.auctionListView indexPathForCell:cell];
    if (indexPath != NULL) {
        Auction* auction = (Auction*)[self.auctionArr objectAtIndex:indexPath.row];
        [self viewAuctionDetail:auction];
    }
}

- (void) viewAuctionDetail:(Auction*) auction {
    [ProgressHUD show];
    [[ServiceManager sharedManager] getAuctionDetail:auction.auct_id completion:^(BOOL bSuccess, Auction* auct, NSString* error) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            [ProgressHUD dismiss];
            if (bSuccess == true) {
                UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                while (topController.presentedViewController) {
                    topController = topController.presentedViewController;
                }
                UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
                NSArray* controllerArr = [navController viewControllers];
                MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
                NSArray* tabControllerArr = mainTabController.viewControllers;
                
                AuctionDetailController* detailController = (AuctionDetailController*)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"auctionDetailController"];
                detailController.auction = auct;
                
                UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
                item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
                [detailController setTabBarItem:item];
                
                [mainTabController setViewControllers:@[tabControllerArr[0], detailController, tabControllerArr[2]] animated:false];
                [mainTabController setSelectedIndex:1];
            } else {
                [ProgressHUD showError:@"Failed to Load Auction Data"];
            }
        }];
    }];
}

- (NSInteger) getDifferenceDate:(NSDate*) endDate {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"MM-dd-yyy"];
    NSDate *startDate = [NSDate date];
    NSCalendar *gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    NSDateComponents *components = [gregorianCalendar components:NSCalendarUnitDay
                                                        fromDate:startDate
                                                          toDate:endDate
                                                         options:0];
    if ([components day] < 0)
        return 0;
    return [components day];
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 225;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
