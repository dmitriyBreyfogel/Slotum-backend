package io.slotum.backend.api.me;

import io.slotum.backend.api.me.dto.*;
import io.slotum.backend.application.usecase.notification.*;
import io.slotum.backend.application.usecase.organization.CreateMyOrganizationUseCase;
import io.slotum.backend.application.usecase.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.usecase.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.application.usecase.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class MeController implements MeApi {
    private final CreateSpecialistUseCase createSpecialistUseCase;
    private final CreateMyOrganizationUseCase createMyOrganizationUseCase;
    private final GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase;
    private final RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase;
    private final GetAllUserNotificationsUseCase getAllUserNotificationsUseCase;
    private final GetUnreadUserNotificationsUseCase getUnreadUserNotificationsUseCase;
    private final CountUnreadUserNotificationsUseCase countUnreadUserNotificationsUseCase;
    private final ReadNotificationUseCase readNotificationUseCase;
    private final ReadAllUnreadUserNotificationUseCase readAllUnreadUserNotificationUseCase;

    public MeController(
            CreateSpecialistUseCase createSpecialistUseCase,
            CreateMyOrganizationUseCase createMyOrganizationUseCase,
            GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase,
            RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase,
            GetAllUserNotificationsUseCase getAllUserNotificationsUseCase,
            GetUnreadUserNotificationsUseCase getUnreadUserNotificationsUseCase,
            CountUnreadUserNotificationsUseCase countUnreadUserNotificationsUseCase,
            ReadNotificationUseCase readNotificationUseCase,
            ReadAllUnreadUserNotificationUseCase readAllUnreadUserNotificationUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.createMyOrganizationUseCase = createMyOrganizationUseCase;
        this.getSpecialistOrganizationsUseCase = getSpecialistOrganizationsUseCase;
        this.removeSpecialistFromOrganizationUseCase = removeSpecialistFromOrganizationUseCase;
        this.getAllUserNotificationsUseCase = getAllUserNotificationsUseCase;
        this.getUnreadUserNotificationsUseCase = getUnreadUserNotificationsUseCase;
        this.countUnreadUserNotificationsUseCase = countUnreadUserNotificationsUseCase;
        this.readNotificationUseCase = readNotificationUseCase;
        this.readAllUnreadUserNotificationUseCase = readAllUnreadUserNotificationUseCase;
    }

    @Override
    public ResponseEntity<SpecialistDto> createSpecialistFromMe(
            AuthenticatedUser currentUser,
            CreateSpecialistRequest request
    ) {
          Specialist result = createSpecialistUseCase.execute(
                  new CreateSpecialistUseCase.Command(
                          currentUser.userId(),
                          request.description()
                  )
          );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SpecialistDto(
                        result.getUserId(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @Override
    public ResponseEntity<OrganizationDto> createMyOrganization(
            AuthenticatedUser currentUser,
            CreateOrganizationRequest request
    ) {
        Organization result = createMyOrganizationUseCase.execute(
                new CreateMyOrganizationUseCase.Command(
                        currentUser.userId(),
                        request.name(),
                        request.description()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrganizationDto(
                        result.getId(),
                        result.getName(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @Override
    public ResponseEntity<List<OrganizationDto>> getMyOrganizations(AuthenticatedUser currentUser) {
        List<Organization> result = getSpecialistOrganizationsUseCase.execute(currentUser.userId());

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(organization -> new OrganizationDto(
                        organization.getId(),
                        organization.getName(),
                        organization.getDescription(),
                        organization.getGrade()
                )).toList()
        );
    }

    @Override
    public ResponseEntity<OrganizationMemberDto> removeMeFromOrganization(
            AuthenticatedUser currentUser,
            Long organizationId
    ) {
        OrganizationMember result = removeSpecialistFromOrganizationUseCase.execute(organizationId, currentUser.userId());

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
                )
        );
    }

    @Override
    public ResponseEntity<List<NotificationDto>> getMyNotifications(AuthenticatedUser currentUser) {
        List<NotificationDto> notifications = getAllUserNotificationsUseCase.execute(currentUser.userId())
                .stream()
                .map(MeController::toNotificationDto)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @Override
    public ResponseEntity<List<NotificationDto>> getMyUnreadNotifications(AuthenticatedUser currentUser) {
        List<NotificationDto> notifications = getUnreadUserNotificationsUseCase.execute(currentUser.userId())
                .stream()
                .map(MeController::toNotificationDto)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @Override
    public ResponseEntity<Long> countMyUnreadNotifications(AuthenticatedUser currentUser) {
        Long count = countUnreadUserNotificationsUseCase.execute(currentUser.userId());
        return ResponseEntity.ok(count);
    }

    @Override
    public ResponseEntity<Void> readMyNotification(
            AuthenticatedUser currentUser,
            Long notificationId
    ) {
        readNotificationUseCase.execute(notificationId, currentUser.userId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Long> readAllMyNotifications(AuthenticatedUser currentUser) {
        Long count = readAllUnreadUserNotificationUseCase.execute(currentUser.userId());
        return ResponseEntity.ok(count);
    }


    private static NotificationDto toNotificationDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
