//
//  AuctionController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "AuctionController.h"
#import "AuctionListController.h"
#import <ProgressHUD/ProgressHUD.h>
#import "ServiceManager.h"
#import "Auction.h"

@interface AuctionController () <RMPScrollingMenuBarControllerDelegate>

{
    NSArray* filter;
    NSMutableArray* controllerArr;
}


@end

@implementation AuctionController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.delegate = self;
    filter = @[@"ALL", @"ACTIVE", @"UPCOMING", @"CANCELLED"];
    
    controllerArr = [NSMutableArray array];
    for (NSInteger index = 0; index < 4; index++) {
        AuctionListController* controller = (AuctionListController*)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"auctionListController"];
        controller.filter = (AUCTION_FILTER) index;
        [controllerArr addObject:controller];
    }
    [self setViewControllers:controllerArr];
    
    self.menuBar.indicatorColor = [UIColor colorWithRed:255 / 255.0f green:126 / 255.0f blue:39 / 255.0f alpha:0.8];
    self.menuBar.showsSeparatorLine = TRUE;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [ProgressHUD show];
    [[ServiceManager sharedManager] getAllAuctions:^(NSArray* auctArr) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            if (auctArr == NULL) {
                [ProgressHUD showError:@"Loading Failed"];
            } else {
                [ServiceManager sharedManager].auctionArr = [NSMutableArray arrayWithArray:auctArr];
                AuctionListController* controller = (AuctionListController*) controllerArr[0];
                controller.auctionArr = [ServiceManager sharedManager].auctionArr;
                [controller.auctionListView reloadData];
                [ProgressHUD dismiss];
            }
        }];
    }];
}

- (void)menuBarController:(RMPScrollingMenuBarController*)menuBarController didSelectViewController:(UIViewController*)viewController {
    NSLog(@"selected index - %ld", menuBarController.selectedIndex);
    NSArray* filterNum = @[@(-1), @(1), @(88), @(7)];
    // NSArray* filterNum = @[@(-1), @(3), @(4), @(0)];
    NSInteger index = menuBarController.selectedIndex;
    AuctionListController* controller = (AuctionListController*) viewController;
    if (index != 0) {
        NSInteger status = [filterNum[index] integerValue];
        controller.auctionArr = [NSMutableArray array];
        for (NSInteger aInd = 0; aInd < [ServiceManager sharedManager].auctionArr.count; aInd++) {
            Auction* auction = [[ServiceManager sharedManager].auctionArr objectAtIndex:aInd];
            if (index < 3) {
                if (auction.status == status) {
                    [controller.auctionArr addObject:auction];
                }
            } else {
                if (auction.status == 6 || auction.status == 7 || auction.status == 8) {
                    [controller.auctionArr addObject:auction];
                }
            }
        }
        [controller.auctionListView reloadData];
    }
}

- (RMPScrollingMenuBarItem*)menuBarController:(RMPScrollingMenuBarController*)menuBarController menuBarItemAtIndex:(NSInteger)index {
    RMPScrollingMenuBarItem* item = [[RMPScrollingMenuBarItem alloc] init];
    item.title = filter[index];
    
    return item;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
