//
//  WelcomeController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "WelcomeController.h"
#import "ServiceManager.h"
#import "BidHistoryCell.h"
#import "ProgressHUD.h"

@interface WelcomeController () <UITableViewDelegate, UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UIView *firstWelcomeView;
@property (weak, nonatomic) IBOutlet UIView *originalWelcomeView;
@property (weak, nonatomic) IBOutlet UILabel *firstTitle;
@property (weak, nonatomic) IBOutlet UILabel *originalTitle;
@property (weak, nonatomic) IBOutlet UITableView *bidListView;


@end

@implementation WelcomeController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [_originalTitle setText:[NSString stringWithFormat:@"Welcome %@!", [ServiceManager sharedManager].userName]];
    
    if ([ServiceManager sharedManager].bLoginFirstTime == true) {
        [_firstWelcomeView setHidden:false];
        [_originalWelcomeView setHidden:true];
    } else {
        [_firstWelcomeView setHidden:true];
        [_originalWelcomeView setHidden:false];
    }
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 2;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    BidHistoryCell* cell = (BidHistoryCell*) [tableView dequeueReusableCellWithIdentifier:@"bidHistoryCell" forIndexPath:indexPath];
    return cell;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}

- (IBAction)onClaimFacility:(id)sender { // Contact Support
    NSString *subject = [NSString stringWithFormat:@"Support Request - Sent From iOS App"];
    NSString *mail = [NSString stringWithFormat:@"support@storageauctions.net"];
    NSCharacterSet *set = [NSCharacterSet URLHostAllowedCharacterSet];
    NSURL *url = [[NSURL alloc] initWithString:[NSString stringWithFormat:@"mailto:?to=%@&subject=%@",
                                                [mail stringByAddingPercentEncodingWithAllowedCharacters:set],
                                                [subject stringByAddingPercentEncodingWithAllowedCharacters:set]]];
    [[UIApplication sharedApplication] openURL:url];
}

- (IBAction)onAddFirstAuction:(id)sender {
    
    // [self performSegueWithIdentifier:@"new_auction" sender:nil];
    [ProgressHUD show];
    [[ServiceManager sharedManager] getAllFacilities:^(NSArray* facArr) {
        if (facArr != NULL && facArr.count > 0) {
            [ServiceManager sharedManager].userID = ((Facility*)facArr[0]).user_id;
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [ProgressHUD dismiss];
                UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
                UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"newAuctionController"];
                [navController pushViewController:controller animated:TRUE];
            }];
        } else {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [ProgressHUD showError:@"You need to create Facility first."];
            }];
        }
    }];
}

- (IBAction)contactSupport:(id)sender { // Create a facility
    UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
    UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"facilityAddressController"];
    [navController pushViewController:controller animated:TRUE];
}

- (IBAction)onCreateNewAuction:(id)sender {
    [self performSegueWithIdentifier:@"new_auction" sender:nil];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
