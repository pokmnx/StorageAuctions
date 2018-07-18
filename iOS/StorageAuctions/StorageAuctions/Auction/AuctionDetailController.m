//
//  AuctionDetailController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 7/12/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "AuctionDetailController.h"
#import "AuctionDetailImageCell.h"
#import "UIViewController+LGSideMenuController.h"
#import "MainTabBarController.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "ServiceManager.h"
#import "ProgressHUD.h"

@interface AuctionDetailController () <UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *bidPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *startDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *endDateLabel;
@property (weak, nonatomic) IBOutlet UICollectionView *imageListView;
@property (weak, nonatomic) IBOutlet UIImageView *firstImageView;


@property (nonatomic) NSMutableArray* imageArray;
@property (nonatomic) NSMutableArray* imageIDArray;

@end

@implementation AuctionDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.titleLabel.text = self.auction.title;
    switch (self.auction.status) {
        case 0:
            self.statusLabel.text = @"Status: INACTIVE";
            break;
        case 1:
            self.statusLabel.text = @"Status: ACTIVE";
            break;
        case 2:
            self.statusLabel.text = @"Status: CLOSED";
            break;
        case 3:
            self.statusLabel.text = @"Status: PAID";
            break;
        case 4:
            self.statusLabel.text = @"Status: UNSOLD";
            break;
        case 5:
            self.statusLabel.text = @"Status: FAILED PAYMENT";
            break;
        case 6:
            self.statusLabel.text = @"Status: CANCELED";
            break;
        case 7:
            self.statusLabel.text = @"Status: CANCELED PAID";
            break;
        case 8:
            self.statusLabel.text = @"Status: CANCELLED FAILED";
            break;
        case 9:
            self.statusLabel.text = @"Status: FAILED FINAL";
            break;
        case 10:
            self.statusLabel.text = @"Status: PENDING";
            break;
        case 11:
            self.statusLabel.text = @"Status: ONSITE";
            break;
        case 88:
            self.statusLabel.text = @"Status: UPCOMING";
            break;
        default:
            self.statusLabel.text = @"Status: PROCESSING";
            break;
    }
    
    if (self.auction.meta != NULL && self.auction.meta.currentBidPrice != 0) {
        self.bidPriceLabel.text = [NSString stringWithFormat:@"Current Bid: $%.2f", self.auction.meta.currentBidPrice];
    } else {
        self.bidPriceLabel.text = @"Current Bid: $0";
    }
    
    self.startDateLabel.text = [NSString stringWithFormat:@"Start Date: %@", [self getStrFrom:self.auction.time_start]];
    self.endDateLabel.text = [NSString stringWithFormat:@"End Date: %@", [self getStrFrom:self.auction.time_end]];
    
    [self initImageArr];
}

- (void) viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    UIBarButtonItem* finish = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStylePlain target:self action:@selector(finishEditing:)];
    self.navigationItem.rightBarButtonItem = finish;
}

- (void) finishEditing:(id) sender {
    UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
    while (topController.presentedViewController) {
        topController = topController.presentedViewController;
    }
    UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
    NSArray* controllerArr = [navController viewControllers];
    MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
    [mainTabController setSelectedIndex:1];
    [navController popToViewController:mainTabController animated:TRUE];
}

- (void) viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationItem setTitle:@"Create New Auction"];
    self.navigationItem.rightBarButtonItem = NULL;
}

- (void) initImageArr {
    self.imageArray = [NSMutableArray array];
    self.imageIDArray = [NSMutableArray array];
    
    if (self.auction.meta != NULL && self.auction.meta.images != NULL && self.auction.meta.images.count > 0) {
        for (NSInteger index = 0; index < self.auction.meta.images.count; index++) {
            [self.imageArray addObject:self.auction.meta.images[index]];
            [self.imageIDArray addObject:self.auction.meta.imageIDs[index]];
        }
        
        [self setFirstImage];
    }
}

- (void) setFirstImage {
    if (self.imageArray.count > 0) {
        id firstImg = self.imageArray[0];
        if ([firstImg isKindOfClass:[NSString class]]) {
            [self.firstImageView sd_setImageWithURL:[NSURL URLWithString:(NSString*) firstImg]];
        } else {
            [self.firstImageView setImage:[UIImage imageWithData:(NSData*) firstImg]];
        }
    } else {
        [self.firstImageView setImage:[UIImage imageNamed:@"no-image"]];
    }
}

- (NSString*) getStrFrom:(NSDate*) date {
    if (date == NULL)
        return NULL;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"M/d/yy"];
    return [formatter stringFromDate:date];
}

- (IBAction)onClickEditDetails:(id)sender {
    [self.navigationController popViewControllerAnimated:TRUE];
}

- (IBAction)onClickUploadPhoto:(id)sender {
    UIAlertController* alertController = [UIAlertController alertControllerWithTitle:@"Upload Photos" message:@"" preferredStyle:UIAlertControllerStyleActionSheet];
    [alertController addAction:[UIAlertAction actionWithTitle:@"Take a Photo" style:UIAlertActionStyleDefault handler:^(UIAlertAction* action) {
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            UIImagePickerController* controller = [[UIImagePickerController alloc] init];
            controller.sourceType = UIImagePickerControllerSourceTypeCamera;
            controller.delegate = self;
            controller.allowsEditing = true;
            [self presentViewController:controller animated:TRUE completion:NULL];
        } else {
            UIAlertController* controller = [UIAlertController alertControllerWithTitle:@"Warning" message:@"You don't have Camera" preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction* okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDestructive handler:NULL];
            [controller addAction:okAction];
            [self presentViewController:controller animated:TRUE completion:NULL];
        }
    }]];
    
    [alertController addAction:[UIAlertAction actionWithTitle:@"Choose a Photo" style:UIAlertActionStyleDefault handler:^(UIAlertAction* action) {
        UIImagePickerController* controller = [[UIImagePickerController alloc] init];
        controller.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        controller.delegate = self;
        controller.allowsEditing = true;
        [self presentViewController:controller animated:TRUE completion:NULL];
    }]];
    
    [alertController addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:NULL]];
    [self presentViewController:alertController animated:FALSE completion:NULL];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {
    UIImage* image = info[UIImagePickerControllerEditedImage];
    NSData* data = UIImageJPEGRepresentation(image, 0);
    [self.imageListView performBatchUpdates:^{
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:self.imageArray.count inSection:0];
        [self.imageListView insertItemsAtIndexPaths:[NSArray arrayWithObject:indexPath]];
        [self.imageArray addObject:data];
    } completion:NULL];
    [self setFirstImage];
    [picker dismissViewControllerAnimated:TRUE completion:^{
        [ProgressHUD show];
        [[ServiceManager sharedManager] setImage:self.auction.auct_id image:data completion:^(BOOL bSuccess, NSString* mediaID, NSString* error) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [ProgressHUD dismiss];
                if (bSuccess == false) {
                    if (error != NULL) {
                        [ProgressHUD showError:error];
                    } else {
                        [ProgressHUD showError];
                    }
                } else {
                    [self.imageIDArray addObject:mediaID];
                }
            }];
        }];
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [picker dismissViewControllerAnimated:TRUE completion:NULL];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.imageArray.count;
}

// The cell that is returned must be retrieved from a call to -dequeueReusableCellWithReuseIdentifier:forIndexPath:
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    AuctionDetailImageCell* cell = (AuctionDetailImageCell*) [collectionView dequeueReusableCellWithReuseIdentifier:@"auctionDetailImageCell" forIndexPath:indexPath];
    id imageData = self.imageArray[indexPath.row];
    
    if ([imageData isKindOfClass:[NSString class]]) {
        [cell.detailImageView sd_setImageWithURL:[NSURL URLWithString:(NSString*) imageData]];
    } else {
        [cell.detailImageView setImage:[UIImage imageWithData:imageData]];
    }
    [cell.deleteBtn addTarget:self action:@selector(deleteImage:) forControlEvents:UIControlEventTouchUpInside];
    
    return cell;
}

- (void) deleteImage:(id) sender {
    
    UICollectionViewCell* cell = (UICollectionViewCell*)[[(UIButton*) sender superview] superview];
    NSIndexPath* indexPath = [_imageListView indexPathForCell:cell];
    if (indexPath != NULL) {
        
        [ProgressHUD show];
        [[ServiceManager sharedManager] deleteImage:self.auction.auct_id mediaID:self.imageIDArray[indexPath.row] completion:^(BOOL bSuccess, NSString* error) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [ProgressHUD dismiss];
                if (bSuccess) {
                    [self.imageListView performBatchUpdates:^{
                        [self.imageListView deleteItemsAtIndexPaths:@[indexPath]];
                        [self.imageArray removeObjectAtIndex:indexPath.row];
                        [self.imageIDArray removeObjectAtIndex:indexPath.row];
                    }completion:NULL];
                    [self setFirstImage];
                } else {
                    [ProgressHUD showError:error];
                }
            }];
        }];
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    CGFloat width = [[UIScreen mainScreen] bounds].size.width;
    CGFloat cellWidth = (width - 10) / 3;
    return CGSizeMake(cellWidth, cellWidth);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
