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

- (IBAction)onClaimFacility:(id)sender {
    
}

- (IBAction)onAddFirstAuction:(id)sender {
    [self performSegueWithIdentifier:@"new_auction" sender:nil];
}

- (IBAction)contactSupport:(id)sender {
    
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
