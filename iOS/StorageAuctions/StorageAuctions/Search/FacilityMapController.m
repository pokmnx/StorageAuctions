//
//  FacilityMapController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "FacilityMapController.h"
#import "FacilityFieldController.h"
@import GoogleMaps;


@interface FacilityMapController ()

@property (weak, nonatomic) IBOutlet UITextField *addressField;
@property (weak, nonatomic) IBOutlet UIView *mapContainer;

@end

@implementation FacilityMapController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [_addressField setText:self.address];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationItem setTitle:@"Add New Facility"];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:self.mapAddress.coordinate.latitude
                                                            longitude:self.mapAddress.coordinate.longitude
                                                                 zoom:18];
    GMSMapView *mapView = [GMSMapView mapWithFrame:CGRectMake(0, 0, self.mapContainer.bounds.size.width, self.mapContainer.bounds.size.height) camera:camera];
    GMSMarker* marker = [GMSMarker markerWithPosition:self.mapAddress.coordinate];
    marker.map = mapView;
    [self.mapContainer addSubview:mapView];
}

- (IBAction)onContinue:(id)sender {
    [self performSegueWithIdentifier:@"facilityDetail" sender:nil];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    [self.navigationItem setTitle:@""];
    FacilityFieldController* controller = (FacilityFieldController*) [segue destinationViewController];
    controller.mapAddress = self.mapAddress;
}

@end
