//
//  FacilityCell.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "FacilityCell.h"
@import GoogleMaps;

#define MAP_TAG 40001

@implementation FacilityCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void) layoutSubviews {
    [super layoutSubviews];
    
    GMSCameraPosition* camera = [GMSCameraPosition cameraWithLatitude:self.lat longitude:self.lon zoom:18];
    GMSMapView* mapView = [GMSMapView mapWithFrame:CGRectMake(0, 0, self.mapContainer.bounds.size.width, self.mapContainer.bounds.size.height) camera:camera];
    
    GMSMarker* marker = [GMSMarker markerWithPosition:CLLocationCoordinate2DMake(self.lat, self.lon)];
    marker.map = mapView;
    [mapView setTag:MAP_TAG];
    
    BOOL bAddedMap = false;
    GMSMapView* currentMapView = NULL;
    for (UIView* sub in self.mapContainer.subviews) {
        if (sub.tag == MAP_TAG) {
            bAddedMap = true;
            currentMapView = (GMSMapView*) sub;
            break;
        }
    }
    
    if (bAddedMap == false) {
        [self.mapContainer addSubview:mapView];
    } else {
        [currentMapView setCamera:camera];
        marker.map = currentMapView;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
