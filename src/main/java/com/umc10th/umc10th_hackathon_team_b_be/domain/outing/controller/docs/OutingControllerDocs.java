package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.controller.docs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.LocationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionCreateRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionEndRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.SunscreenApplicationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.global.config.SwaggerErrorExamples;
import com.umc10th.umc10th_hackathon_team_b_be.global.config.SwaggerSuccessExamples;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "Outing", description = "외출 세션 시작, 조회, 차단제 기록, 종료 API")
public interface OutingControllerDocs {

    @Operation(
            summary = "외출 세션 시작",
            description = "자외선 차단제 선택값과 현재 위치를 기준으로 외출 세션을 시작합니다. 성공 시 nextScreen=OUTING과 외출 화면 데이터를 반환하며, 20:00 이후처럼 외출 시작이 제한되는 시간에는 OUTING_400을 반환합니다.",
            operationId = "createOutingSession"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외출 세션 생성 성공 및 외출 화면 데이터 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "OUTING", value = SwaggerSuccessExamples.OUTING_FLOW_OUTING)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "외출 시작 제한 시간 또는 잘못된 요청값",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400),
                                    @ExampleObject(name = "OUTING_400", value = SwaggerErrorExamples.OUTING_400)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 진행 중인 외출 세션이 있는 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "OUTING_409", value = SwaggerErrorExamples.OUTING_409)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "502",
                    description = "날씨 또는 위치 외부 API 조회에 실패한 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "WEATHER_502", value = SwaggerErrorExamples.WEATHER_502)
                    )
            )
    })
    ResponseEntity<ApiResponse<OutingFlowResponse>> createOutingSession(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "외출 세션 시작 요청. sunscreenAppliedOption은 마지막 차단제 사용 시점을 의미합니다.",
                    required = true
            )
            OutingSessionCreateRequest request
    );

    @Operation(
            summary = "외출 모드 화면 조회 및 새로고침",
            description = "진행 중인 외출 세션을 최신 위치/날씨/자외선 정보로 재계산합니다. 세션이 autoEndAt을 지난 경우 조회 대신 자동 종료 처리하고 nextScreen=HOME, endedSession, autoEndNotice를 반환합니다.",
            operationId = "getCurrentOutingSession"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외출 화면 데이터 또는 자동 종료 결과 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "OUTING", value = SwaggerSuccessExamples.OUTING_FLOW_OUTING),
                                    @ExampleObject(name = "AUTO_ENDED", value = SwaggerSuccessExamples.OUTING_FLOW_AUTO_ENDED)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "위도 또는 경도 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "진행 중인 외출 세션이 없는 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "OUTING_404", value = SwaggerErrorExamples.OUTING_404)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "502",
                    description = "날씨 또는 위치 외부 API 조회에 실패한 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "WEATHER_502", value = SwaggerErrorExamples.WEATHER_502)
                    )
            )
    })
    ResponseEntity<ApiResponse<OutingFlowResponse>> getCurrentOutingSession(
            @Parameter(hidden = true) Long userId,
            @ParameterObject LocationRequest request
    );

    @Operation(
            summary = "자외선 차단제 다시 바르기 기록",
            description = "현재 시각으로 차단제 사용 기록을 저장한 뒤 외출 화면 데이터를 재계산합니다. autoEndAt을 지난 경우 차단제 기록은 저장하지 않고 세션을 AUTO 사유로 종료합니다.",
            operationId = "applySunscreen"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "차단제 기록 성공 및 재계산된 외출 화면 데이터 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "OUTING", value = SwaggerSuccessExamples.OUTING_FLOW_OUTING),
                                    @ExampleObject(name = "AUTO_ENDED", value = SwaggerSuccessExamples.OUTING_FLOW_AUTO_ENDED)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "차단제 기록 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "진행 중인 외출 세션이 없는 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "OUTING_404", value = SwaggerErrorExamples.OUTING_404)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "502",
                    description = "날씨 또는 위치 외부 API 조회에 실패한 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "WEATHER_502", value = SwaggerErrorExamples.WEATHER_502)
                    )
            )
    })
    ResponseEntity<ApiResponse<OutingFlowResponse>> applySunscreen(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "자외선 차단제 다시 바르기 요청. 현재 위치 기반으로 날씨와 계란 상태를 다시 계산합니다.",
                    required = true
            )
            SunscreenApplicationRequest request
    );

    @Operation(
            summary = "외출 세션 직접 종료",
            description = "사용자가 진행 중인 외출 세션을 직접 종료합니다. 정상 종료 시 nextScreen=HOME과 endedSession을 반환하며, autoEndAt을 지난 경우 수동 종료 대신 AUTO 사유로 자동 종료됩니다.",
            operationId = "completeOutingSession"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수동 종료 결과 또는 자동 종료 결과 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "MANUAL_ENDED", value = SwaggerSuccessExamples.OUTING_FLOW_ENDED),
                                    @ExampleObject(name = "AUTO_ENDED", value = SwaggerSuccessExamples.OUTING_FLOW_AUTO_ENDED)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 종료 상태 요청 또는 잘못된 요청값",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400),
                                    @ExampleObject(name = "OUTING_400", value = SwaggerErrorExamples.OUTING_400)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "종료할 외출 세션이 없거나 사용자 소유가 아닌 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "OUTING_404", value = SwaggerErrorExamples.OUTING_404)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "502",
                    description = "날씨 또는 위치 외부 API 조회에 실패한 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "WEATHER_502", value = SwaggerErrorExamples.WEATHER_502)
                    )
            )
    })
    ResponseEntity<ApiResponse<OutingFlowResponse>> completeOutingSession(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "종료할 외출 세션 ID", example = "10") Long outingSessionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "외출 세션 직접 종료 요청. status는 COMPLETED만 사용합니다.",
                    required = true
            )
            OutingSessionEndRequest request
    );
}
