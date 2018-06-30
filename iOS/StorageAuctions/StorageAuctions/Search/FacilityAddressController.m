//
//  FacilityAddressController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "FacilityAddressController.h"
#import "FacilityMapController.h"
#import "ServiceManager.h"
#import "ProgressHUD.h"
#import "LMGeocoder.h"


@interface FacilityAddressController ()

{
    LMAddress* mapAddress;
}

@property (weak, nonatomic) IBOutlet UITextField *addressField;


@end

@implementation FacilityAddressController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.navigationController.navigationBar setTintColor:[UIColor whiteColor]];
    [self.navigationItem setTitle:@"Add New Facility"];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSForegroundColorAttributeName:[UIColor whiteColor]}];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationItem setTitle:@"Add New Facility"];
}


- (IBAction)onClickSearch:(id)sender {
    [ProgressHUD show];
    
    [[LMGeocoder sharedInstance] geocodeAddressString:_addressField.text
                                              service:kLMGeocoderGoogleService
                                    completionHandler:^(NSArray *results, NSError *error) {
                                        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                                            if (results.count && !error) {
                                                [ProgressHUD dismiss];
                                                mapAddress = [results firstObject];
                                                [self performSegueWithIdentifier:@"facilityMap" sender:nil];
                                            } else {
                                                [ProgressHUD showError:error.description];
                                            }
                                        }];
                                    }];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    [self.navigationItem setTitle:@""];
    
    FacilityMapController* controller = (FacilityMapController*) [segue destinationViewController];
    controller.mapAddress = mapAddress;
    controller.address = _addressField.text;
}

@end
