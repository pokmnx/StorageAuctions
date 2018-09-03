//
//  MenuController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "MenuController.h"
#import "ServiceManager.h"
#import "UIViewController+LGSideMenuController.h"
#import "MainTabBarController.h"
#import "HomeController.h"
#import "AuctionController.h"

@interface MenuController ()

@end

@implementation MenuController

- (void)viewDidLoad {
    [super viewDidLoad];
    [ServiceManager sharedManager].mainMenuController = self.sideMenuController;
}

- (IBAction)onClickHome:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Home Clicked");
        UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (topController.presentedViewController) {
            topController = topController.presentedViewController;
        }
        UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
        NSArray* controllerArr = [navController viewControllers];

        MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
/*
        HomeController* homeController = (HomeController*)[[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"homeController"];
        
        NSArray* tabControllerArr = [mainTabController viewControllers];
        NSArray* newTabContArr = @[homeController, tabControllerArr[1], tabControllerArr[2]];
        
        [mainTabController setViewControllers:newTabContArr];
*/
        [mainTabController setSelectedIndex:0];
        [navController popToViewController:mainTabController animated:TRUE];
    }];
}

- (IBAction)onClickYourAuctions:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Your Auctions Clicked");
        UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (topController.presentedViewController) {
            topController = topController.presentedViewController;
        }
        UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
        NSArray* controllerArr = [navController viewControllers];
        MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
        
        
        if ([mainTabController.viewControllers[1] isKindOfClass:[AuctionController class]] == false) {
            AuctionController* auctionController = (AuctionController*) [[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"auctionController"];
            
            UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
            item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
            [auctionController setTabBarItem:item];
            
            NSArray* array = @[mainTabController.viewControllers[0], auctionController, mainTabController.viewControllers[2]];
            [mainTabController setViewControllers:array];
        }
        
        [mainTabController setSelectedIndex:1];
        [navController popToViewController:mainTabController animated:TRUE];
    }];
}

- (IBAction)onListAuction:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"List Auction Clicked");
        UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
        UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"newAuctionController"];
        [navController pushViewController:controller animated:TRUE];
    }];
}

- (IBAction)onAddNewFacility:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Add New Facility Clicked");
        UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
        UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"facilityAddressController"];
        [navController pushViewController:controller animated:TRUE];
    }];
}

- (IBAction)onReports:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Reports Clicked");
    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
