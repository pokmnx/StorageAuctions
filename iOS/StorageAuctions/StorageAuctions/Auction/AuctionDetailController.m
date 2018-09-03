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
#import "NewAuctionController.h"

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
            self.statusLabel.text = @"INACTIVE";
            break;
        case 1:
            self.statusLabel.text = @"ACTIVE";
            break;
        case 2:
            self.statusLabel.text = @"CLOSED";
            break;
        case 3:
            self.statusLabel.text = @"PAID";
            break;
        case 4:
            self.statusLabel.text = @"UNSOLD";
            break;
        case 5:
            self.statusLabel.text = @"FAILED PAYMENT";
            break;
        case 6:
            self.statusLabel.text = @"CANCELED";
            break;
        case 7:
            self.statusLabel.text = @"CANCELED PAID";
            break;
        case 8:
            self.statusLabel.text = @"CANCELLED FAILED";
            break;
        case 9:
            self.statusLabel.text = @"FAILED FINAL";
            break;
        case 10:
            self.statusLabel.text = @"PENDING";
            break;
        case 11:
            self.statusLabel.text = @"ONSITE";
            break;
        case 88:
            self.statusLabel.text = @"UPCOMING";
            break;
        default:
            self.statusLabel.text = @"PROCESSING";
            break;
    }
    
    if (self.auction.meta != NULL && self.auction.meta.currentBidPrice != 0) {
        self.bidPriceLabel.text = [NSString stringWithFormat:@"$%.2f", self.auction.meta.currentBidPrice];
    } else {
        self.bidPriceLabel.text = @"$0";
    }
    
    self.startDateLabel.text = [NSString stringWithFormat:@"%@", [self getStrFrom:self.auction.time_start]];
    self.endDateLabel.text = [NSString stringWithFormat:@"%@", [self getStrFrom:self.auction.time_end]];
    
    [self initImageArr];
}

- (void) viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
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
    
    AuctionRequest* request = [[AuctionRequest alloc] init];
    request.amount_owed = self.auction.amount_owed;
    request.alt_unit_number = [NSString stringWithFormat:@"%ld", self.auction.alt_unit_number];
    request.reserve = [NSString stringWithFormat:@"%f", self.auction.reserve];
    
    NSDateFormatter* formatter2 = [[NSDateFormatter alloc] init];
    [formatter2 setDateFormat:@"yyyy-M-d"];
    [formatter2 setDateFormat:@"yyyy-M-d hh:mma"];
    [formatter2 setAMSymbol:@"am"];
    [formatter2 setPMSymbol:@"pm"];
    
    request.time_end = [formatter2 stringFromDate:self.auction.time_end];
    request.terms = self.auction.terms;
    request.lock_tag = self.auction.lock_tag;
    request.fees = [NSString stringWithFormat:@"%f", self.auction.fees];
    request.batch_email = [NSString stringWithFormat:@"%ld", self.auction.batch_email];
    if (self.auction.meta != NULL) {
        request.cleanout = [NSString stringWithFormat:@"%ld", self.auction.meta.cleanout];
        request.cleanout_other = self.auction.meta.cleanout_other;
        request.payment_other = self.auction.meta.payment_other;
    }
    
    request.time_st_zone = self.auction.time_st_zone;
    request.time_start = [formatter2 stringFromDate:self.auction.time_start];
    request.facility_id = [NSString stringWithFormat:@"%ld", self.auction.facility_id];
    request.unit_size = [NSString stringWithFormat:@"%ld", self.auction.unit_size];
    request.descr = self.auction.description;
    request.unit_number = [NSString stringWithFormat:@"%ld", self.auction.unit_number];
    request.time_end_zone = self.auction.time_end_zone;
    request.tenant_name = self.auction.tenant_name;
    
    request.access = @"";
    request.auction_type = @"0";
    request.save_terms = @"0";
    request.reserve = @"0";
    request.payment = @"15";
    request.auct_id = [NSString stringWithFormat:@"%ld", _auction.auct_id];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [[ServiceManager sharedManager] setAuction:request completion:^(BOOL bSuccess, Auction* auction, NSString* error) {
            if (bSuccess == false) {
                NSLog(@"Set Auction Failed");
            } else {
                NSLog(@"Set Auction Success");
            }
        }];
    });
}

- (void) initImageArr {
    self.imageArray = [NSMutableArray array];
    self.imageIDArray = [NSMutableArray array];
    
    if (self.auction.mediaArray != NULL) {
        for (NSInteger index = 0; index < self.auction.mediaArray.count; index++) {
            AuctionMedia* media = self.auction.mediaArray[index];
            if (media.url0 != NULL && media.url0.length > 0)
                [self.imageArray addObject:media.url0];
            else if (media.url1 != NULL && media.url1.length > 0)
                [self.imageArray addObject:media.url1];
            else if (media.url2 != NULL && media.url2.length > 0)
                [self.imageArray addObject:media.url0];
            else if (media.url3 != NULL && media.url3.length > 0)
                [self.imageArray addObject:media.url3];
            
            [self.imageIDArray addObject:[NSString stringWithFormat:@"%ld", media.mediaID]];
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
        [self.firstImageView setImage:[UIImage imageNamed:@"auction_background"]];
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
    // [self.navigationController popViewControllerAnimated:TRUE];
    UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
    NewAuctionController* auctController = (NewAuctionController*)[[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"newAuctionController"];
    auctController.auction = self.auction;
    auctController.bEdit = true;
    [navController pushViewController:auctController animated:true];
}

- (IBAction)onClickUploadPhoto:(id)sender {
    [self showUploadDialog];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {
    UIImage* image = info[UIImagePickerControllerEditedImage];
    NSData* data = UIImageJPEGRepresentation(image, 0);
    [self.imageArray addObject:data];
    [self setFirstImage];
    [self.imageListView reloadData];
    [picker dismissViewControllerAnimated:TRUE completion:^{
        [ProgressHUD show];
        [[ServiceManager sharedManager] setImage:self.auction.auct_id image:data completion:^(BOOL bSuccess, NSString* mediaID, NSString* error) {
            if (bSuccess) {
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    if (bSuccess == false) {
                        if (error != NULL) {
                            [ProgressHUD showError:error];
                        } else {
                            [ProgressHUD showError];
                        }
                    } else {
                        [self.imageIDArray addObject:mediaID];
                        [ProgressHUD showSuccess:@"Image Uploaded"];
                    }
                }];
            } else {
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    if (error != NULL) {
                        [ProgressHUD showError:error];
                    } else {
                        [ProgressHUD showError];
                    }
                }];
            }
        }];
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [picker dismissViewControllerAnimated:TRUE completion:NULL];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    
    if (self.imageArray.count > 6)
        return self.imageArray.count;
    else
        return 6;
}

// The cell that is returned must be retrieved from a call to -dequeueReusableCellWithReuseIdentifier:forIndexPath:
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    AuctionDetailImageCell* cell = (AuctionDetailImageCell*) [collectionView dequeueReusableCellWithReuseIdentifier:@"auctionDetailImageCell" forIndexPath:indexPath];
    
    if (indexPath.row < self.imageArray.count) {
        [cell.deleteBtn setHidden:false];
        id imageData = self.imageArray[indexPath.row];
        if ([imageData isKindOfClass:[NSString class]]) {
            [cell.detailImageView sd_setImageWithURL:[NSURL URLWithString:(NSString*) imageData]];
        } else {
            [cell.detailImageView setImage:[UIImage imageWithData:imageData]];
        }
        [cell.deleteBtn addTarget:self action:@selector(deleteImage:) forControlEvents:UIControlEventTouchUpInside];
    }
    else {
        [cell.detailImageView setImage:[UIImage imageNamed:@"no-image"]];
        [cell.deleteBtn setHidden:true];
        if (self.imageArray.count == 0 && indexPath.row == 0) {
            [cell.detailImageView setImage:[UIImage imageNamed:@"auction_background"]];
        }
    }
    
    return cell;
}

- (void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row >= self.imageArray.count) {
        [self showUploadDialog];
    }
}

- (void) showUploadDialog {
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

- (void) deleteImage:(id) sender {
    
    UICollectionViewCell* cell = (UICollectionViewCell*)[[(UIButton*) sender superview] superview];
    NSIndexPath* indexPath = [_imageListView indexPathForCell:cell];
    if (indexPath != NULL) {
        
        [ProgressHUD show];
        [[ServiceManager sharedManager] deleteImage:self.auction.auct_id mediaID:self.imageIDArray[indexPath.row] completion:^(BOOL bSuccess, NSString* error) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [ProgressHUD dismiss];
                if (bSuccess) {
                    [self.imageArray removeObjectAtIndex:indexPath.row];
                    [self.imageIDArray removeObjectAtIndex:indexPath.row];
                    [self.imageListView reloadData];
                    [self setFirstImage];
                    [ProgressHUD showSuccess:@"Image Deleted"];
                } else {
                    [ProgressHUD showError:error];
                }
            }];
        }];
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    CGFloat width = [[UIScreen mainScreen] bounds].size.width;
    CGFloat cellWidth = (width - 5) / 3;
    return CGSizeMake(cellWidth, cellWidth);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
