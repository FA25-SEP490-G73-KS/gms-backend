package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ServiceRatingRequest;
import fpt.edu.vn.gms.dto.response.ServiceRatingResponse;

public interface ServiceRatingService {

    ServiceRatingResponse createRating(ServiceRatingRequest request);
}

