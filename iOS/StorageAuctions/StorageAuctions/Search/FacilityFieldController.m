//
//  FacilityFieldController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "FacilityFieldController.h"
#import "ServiceManager.h"
#import "ProgressHUD.h"
#import "UIViewController+LGSideMenuController.h"
#import "MainTabBarController.h"

@interface FacilityFieldController ()

@property (weak, nonatomic) IBOutlet UITextField *storageFacilityName;
@property (weak, nonatomic) IBOutlet UITextField *phoneNumber;
@property (weak, nonatomic) IBOutlet UITextField *siteContactEmail;
@property (weak, nonatomic) IBOutlet UITextField *taxRate;
@property (weak, nonatomic) IBOutlet UITextField *commission;


@end

@implementation FacilityFieldController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [_storageFacilityName setText:@"bobs storage"];
    [_phoneNumber setText:@"1234567890"];
    [_siteContactEmail setText:@"abc@gmail.com"];
    [_taxRate setText:@"3"];
    [_commission setText:@"6"];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationItem setTitle:@"Add New Facility"];
}

- (IBAction)onSave:(id)sender {
    CGFloat taxRate = [[_taxRate text] floatValue];
    CGFloat commissionRate = [[_commission text] floatValue];
    [ProgressHUD show];
    [[ServiceManager sharedManager] addNewFacilityWith:_storageFacilityName.text email:_siteContactEmail.text website:@"" phone:_phoneNumber.text taxRate:taxRate commissionRate:commissionRate terms:@"" address:self.mapAddress completion:^(BOOL bSuccess, NSString* error) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            [ProgressHUD dismiss];
            if (bSuccess == true) {
                UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                while (topController.presentedViewController) {
                    topController = topController.presentedViewController;
                }
                UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
                NSArray* controllerArr = [navController viewControllers];
                MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[controllerArr.count - 4];
                [mainTabController setSelectedIndex:2];
                [navController popToViewController:mainTabController animated:TRUE];
            } else {
                [ProgressHUD showError:error];
            }
        }];
    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
