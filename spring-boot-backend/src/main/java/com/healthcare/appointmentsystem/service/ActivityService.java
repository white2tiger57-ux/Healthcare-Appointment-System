package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.dto.ActivityRequest;
import com.healthcare.appointmentsystem.dto.ActivityResponse;
import com.healthcare.appointmentsystem.entity.Activity;
import com.healthcare.appointmentsystem.entity.User;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.repository.ActivityRepository;
import com.healthcare.appointmentsystem.repository.UserRepository;
import com.healthcare.appointmentsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request, CustomUserDetails currentUser) {
        User createdBy = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        User handledBy = null;
        if (request.getHandledById() != null) {
            handledBy = userRepository.findById(request.getHandledById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getHandledById()));
        }

        Activity activity = Activity.builder()
                .action(request.getAction())
                .detail(request.getDetail())
                .timestamp(Instant.now().toEpochMilli())
                .createdBy(createdBy)
                .handledBy(handledBy)
                .build();

        activity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Transactional
    public ActivityResponse updateActivity(Long id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        if (request.getAction() != null) activity.setAction(request.getAction());
        if (request.getDetail() != null) activity.setDetail(request.getDetail());
        if (request.getHandledById() != null) {
            User handledBy = userRepository.findById(request.getHandledById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getHandledById()));
            activity.setHandledBy(handledBy);
        }
        // timestamp is not updated on edit (per PDF example)
        activity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public ActivityResponse getActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        return mapToResponse(activity);
    }

    private ActivityResponse mapToResponse(Activity a) {
        return ActivityResponse.builder()
                .id(a.getId())
                .action(a.getAction())
                .detail(a.getDetail())
                .timestamp(a.getTimestamp())
                .createdBy(ActivityResponse.UserInfo.builder()
                        .id(a.getCreatedBy().getId())
                        .name(a.getCreatedBy().getFullName())
                        .email(a.getCreatedBy().getEmail())
                        .build())
                .handledBy(a.getHandledBy() != null ? ActivityResponse.UserInfo.builder()
                        .id(a.getHandledBy().getId())
                        .name(a.getHandledBy().getFullName())
                        .email(a.getHandledBy().getEmail())
                        .build() : null)
                .fileUrl(a.getFileUrl())
                .build();
    }
}