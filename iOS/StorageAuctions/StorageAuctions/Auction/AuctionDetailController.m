//
//  AuctionDetailController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 7/12/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "AuctionDetailController.h"

@interface AuctionDetailController ()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *bidPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *startDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *endDateLabel;
@property (weak, nonatomic) IBOutlet UICollectionView *imageListView;



@end

@implementation AuctionDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)onClickEditDetails:(id)sender {
    
}

- (IBAction)onClickUploadPhoto:(id)sender {
    
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
