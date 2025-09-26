package com.kh.ypjp.community.recipe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dao.UserRecipeDao;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.BookmarkResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientJsonDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.OfficialRecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.OfficialRecipeResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeWriteRequest;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewPageResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewResponseDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewWriteRequest;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewWriterDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
import com.kh.ypjp.community.recipe.model.vo.CookingStep;
import com.kh.ypjp.community.recipe.model.vo.RcpDetail;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;
import com.kh.ypjp.community.recipe.model.vo.RcpIngs;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;
import com.kh.ypjp.community.recipe.model.vo.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRecipeServiceImpl implements UserRecipeService {

	private final UserRecipeDao dao;
    private final ObjectMapper objectMapper;
    private final UtilService utilService;

    @Override
    public RecipePage selectRecipePage(HashMap<String, Object> params) {
        // 1. 페이지당 보여줄 게시글 수 설정 (예: 8개)
        int pageSize = 12;
        params.put("pageSize", pageSize);
        
        String ingredientsParam = (String) params.get("ingredients");
        if (ingredientsParam != null && !ingredientsParam.isEmpty()) {
            // 쉼표(,)를 기준으로 문자열을 잘라 List로 만듭니다.
            List<String> ingredientList = Arrays.asList(ingredientsParam.split(","));
            // Mapper에서 사용할 수 있도록 "ingredientList"라는 새로운 키로 Map에 추가합니다.
            params.put("ingredientList", ingredientList);
        }
        
        // 2. 전체 게시글 수 조회 (필터링 조건 포함)
        long totalElements = dao.selectRecipeCount(params);
        
        // 3. 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        // 4. 현재 페이지의 게시글 목록 조회
        List<UserRecipeResponse> recipes = dao.selectRecipeList(params);
        for (UserRecipeResponse recipe : recipes) {
        	String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/images/")
	                .path(recipe.getServerName())
	                .toUriString();
        	recipe.setServerName(imageUrl);
        	String profileImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/images/")
	                .path(recipe.getUserProfileImage())
	                .toUriString();
        	recipe.setUserProfileImage(profileImageUrl);
        }
        
        // 5. RecipePage 객체로 포장하여 반환
        return new RecipePage(recipes, totalPages, totalElements);
    }

    @Override
    public List<UserRecipeResponse> selectRankingRecipes() {
    	List<UserRecipeResponse> rankingList = dao.selectRankingRecipes();
        
        // 이미지 전체 URL 생성 로직
        for (UserRecipeResponse recipe : rankingList) {
        	if(recipe.getServerName() != null && !recipe.getServerName().isEmpty()) {
        		// createFullUrl 메소드는 기존 ServiceImpl 클래스에 이미 존재합니다.
        		recipe.setServerName(createFullUrl(recipe.getServerName()));
        	}
        	if(recipe.getUserProfileImage() != null && !recipe.getUserProfileImage().isEmpty()) {
        		recipe.setUserProfileImage(createFullUrl(recipe.getUserProfileImage()));
        	}
        }
        return rankingList;
    }

    @Override
    public List<RcpMethod> selectRcpMethods() {
        return dao.selectRcpMethods();
    }

    @Override
    public List<RcpSituation> selectRcpSituations() {
        return dao.selectRcpSituations();
    }

    @Override
    public List<IngredientInfo> searchIngredients(String keyword) {
        return dao.searchIngredients(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRecipe(RecipeWriteRequest request, long userNo) throws Exception {
        
    	 // 1. DB에서 다음에 사용할 rcpNo를 미리 가져옵니다.
        long rcpNo = dao.getNextRcpNo();
        
        long mainImageNo = 0;
        
        String projectRoot = System.getProperty("user.dir");

        // 2. 대표 이미지 저장
        MultipartFile mainImageFile = request.getMainImage();
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
        	String webPath = "community/recipe/" + rcpNo;
            String changeName = utilService.getChangeName(mainImageFile, webPath);
            
            String serverNameForDb = webPath + "/" + changeName;
            
            Map<String, Object> imageParam = new HashMap<>();
            imageParam.put("originName", mainImageFile.getOriginalFilename());
            imageParam.put("serverName", serverNameForDb);
            
            utilService.insertImage(imageParam);
            mainImageNo = utilService.getImageNo(imageParam);
        }
        
        
        // 3. 영양 정보 및 레시피 정보 저장
        Nutrient totalNutrient = calculateTotalNutrients(request.getIngredients());
        dao.insertNutrient(totalNutrient);

        Recipe recipe = new Recipe();
        recipe.setRcpNo((int)rcpNo); // 미리 받아온 rcpNo 사용
        recipe.setUserNo(userNo);
        recipe.setRcpName(request.getRcpName());
        recipe.setRcpInfo(request.getRcpInfo());
        recipe.setTag(request.getTag());
        recipe.setRcpMthNo(request.getRcpMthNo());
        recipe.setRcpStaNo(request.getRcpStaNo());
        recipe.setImageNo((int)mainImageNo);
        recipe.setNutrientNo(totalNutrient.getNutrientNo()); 
        
        dao.insertRecipe(recipe);

        // 4. 재료 정보 저장
        List<IngredientJsonDto> ingredients = objectMapper.readValue(request.getIngredients(), new TypeReference<>() {});
        for (IngredientJsonDto dto : ingredients) {
            RcpIngredient ing = new RcpIngredient();
            ing.setRcpNo((int)rcpNo);
            ing.setIngNo(dto.getIngNo());
            ing.setQuantity(dto.getQuantity());
            ing.setWeight(dto.getWeight());
            dao.insertRcpIngredient(ing);
        }

        // 5. 요리 순서 저장
        List<String> descriptions = request.getStepDescriptions();
        List<MultipartFile> images = request.getStepImages();
        for (int i = 0; i < descriptions.size(); i++) {
            RcpDetail detail = new RcpDetail();
            detail.setRcpNo((int)rcpNo);
            detail.setRcpOrder(i + 1);
            detail.setDescription(descriptions.get(i));
            
            MultipartFile stepImgFile = images.get(i);
            if (stepImgFile != null && !stepImgFile.isEmpty()) {
            	String webPath = "community/recipe/" + rcpNo;
                String changeName = utilService.getChangeName(stepImgFile, webPath);
                String serverNameForDb = webPath + "/" + changeName;
                
                Map<String, Object> imageParam = new HashMap<>();
                imageParam.put("originName", stepImgFile.getOriginalFilename());
                imageParam.put("serverName", serverNameForDb);
                
                // ✨ 동일한 로직으로 순서 이미지도 처리합니다.
                utilService.insertImage(imageParam);
                long stepImageNo = utilService.getImageNo(imageParam);
                detail.setImageNo((int)stepImageNo);
            }
            dao.insertRcpDetail(detail);
        }
    }
    
    private Nutrient calculateTotalNutrients(String ingredientsJson) throws IOException {
        // 1. 프론트에서 보낸 JSON을 IngredientJsonDto 리스트로 변환
        List<IngredientJsonDto> ingredientList = objectMapper.readValue(ingredientsJson, new TypeReference<>() {});
        
        Nutrient total = new Nutrient();
        total.setEnergy(0);
        total.setCarb(0);
        total.setProtein(0);
        total.setFat(0);
        total.setSodium(0);

        // 2. 각 재료에 대해 루프를 돌면서 영양 정보 계산
        for (IngredientJsonDto dto : ingredientList) {
            // 3. DAO를 통해 DB에서 100g 기준 영양 정보를 조회
            Nutrient baseNutrient = dao.findNutrientsByIngNo(dto.getIngNo());
            
            if (baseNutrient != null) {
                // 4. 사용자가 입력한 중량(weight)에 맞춰 영양성분 환산 및 누적
                double ratio = (double) dto.getWeight() / 100.0;
                total.setEnergy(total.getEnergy() + baseNutrient.getEnergy() * ratio);
                total.setCarb(total.getCarb() + baseNutrient.getCarb() * ratio);
                total.setProtein(total.getProtein() + baseNutrient.getProtein() * ratio);
                total.setFat(total.getFat() + baseNutrient.getFat() * ratio);
                total.setSodium(total.getSodium() + baseNutrient.getSodium() * ratio);
            }
        }
        return total;
    }

    @Override
    @Transactional
    public RecipeDetailResponse selectRecipeDetail(int rcpNo, Long userNo, boolean increaseViewCount) {
    	if (increaseViewCount) {
    		dao.increaseViewCount(rcpNo);
    	}
    	
        Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("userNo", userNo);
        
        RecipeDetailResponse recipe = dao.selectRecipeDetail(params);
        
        if (recipe == null) {
            return null;
        }
        
        // 이전에 만들어두신 findLike 쿼리를 활용
        if (userNo != null) { // 비로그인 사용자가 아닐 경우에만 체크
        	String likeStatus = dao.findLikeStatus(params); 
            recipe.setMyLikeStatus(likeStatus);
        }

        recipe.setIngredients(dao.selectIngredientsByRcpNo(rcpNo));
        recipe.setSteps(dao.selectStepsByRcpNo(rcpNo));

        // 대표 이미지
        if (recipe.getMainImage() != null && !recipe.getMainImage().isEmpty()) {
            recipe.setMainImage(createFullUrl(recipe.getMainImage()));
        }

        //작성자 프로필 이미지
        if (recipe.getWriter() != null && recipe.getWriter().getProfileImage() != null && !recipe.getWriter().getProfileImage().isEmpty()) {
            recipe.getWriter().setProfileImage(createFullUrl(recipe.getWriter().getProfileImage()));
        }
        
        // 요리 순서(Steps) 이미지 
        if (recipe.getSteps() != null) {
            for (CookingStep step : recipe.getSteps()) {
                if (step.getServerName() != null && !step.getServerName().isEmpty()) {
                    step.setServerName(createFullUrl(step.getServerName()));
                }
            }
        }
        
        return recipe;
    }
    
    // 이미지 경로를 완성된 URL로 만들어주는 private 헬퍼 메소드
    private String createFullUrl(String serverName) {
    	
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(serverName)
                .toUriString();
    }
    
    
    
    //좋아요 기능
    @Override
    @Transactional
    public UserRecipeDto.LikeResponse toggleLike(int rcpNo, long userNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("userNo", userNo);

        boolean isLiked;
        
        // 1. 이미 좋아요를 눌렀는지 확인
        int likeCount = dao.findLike(params);
        
        if (likeCount > 0) {
            // 2. 이미 눌렀다면 좋아요 삭제
            dao.deleteLike(params);
            isLiked = false;
        } else {
            // 3. 누르지 않았다면 좋아요 추가
            dao.insertLike(params);
            isLiked = true;
        }
        
        // 4. 최신 좋아요 총 개수 조회
        int totalLikes = dao.countLikes(rcpNo);
        
        // 5. 결과를 DTO에 담아 반환
//        return new UserRecipeDto.LikeResponse(totalLikes, isLiked);
        return null;
    }

	@Override
	public void updateLikeStatus(int rcpNo, Long userNo, String status) {
		Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("userNo", userNo);
        params.put("status", status);
        
        dao.mergeLikeStatus(params);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
	public void createReview(int rcpNo, long userNo, ReviewWriteRequest request) {
		
		long imageNo = 0;
		
		// 리뷰 이미지 저장 (이미지가 있을 경우에만)
        MultipartFile imageFile = request.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            String webPath = "community/recipe/review/" + rcpNo; // 리뷰 이미지 저장 경로
            String changeName = utilService.getChangeName(imageFile, webPath);
            String serverNameForDb = webPath + "/" + changeName;
            
            Map<String, Object> imageParam = new HashMap<>();
            imageParam.put("originName", imageFile.getOriginalFilename());
            imageParam.put("serverName", serverNameForDb);
            
            utilService.insertImage(imageParam);
            imageNo = utilService.getImageNo(imageParam);
        }

        // 2. Review 객체 생성 및 데이터 설정
        Review review = new Review();
        review.setRefNo(rcpNo);      // 어느 레시피에 대한 리뷰인지
        review.setUserNo(userNo);      // 누가 작성했는지
        review.setContent(request.getContent()); // 내용
        review.setStars(request.getStars());       // 별점
        review.setImageNo((int)imageNo);         // 이미지 번호
        review.setRcpSource("COMM"); // '사용자 레시피'에 대한 리뷰임을 표시

        // 3. DAO를 호출하여 DB에 저장
        dao.insertReview(review);
		
		
		
	}

	@Override
	public ReviewPageResponse selectReviewPage(int rcpNo, int page, String sort) {
		 // 1. 한 페이지에 보여줄 리뷰 개수 
        int pageSize = 5;
        long totalReviews = dao.selectReviewCount(rcpNo);
        int totalPages = (int) Math.ceil((double) totalReviews / pageSize);

        // 4. DB로 보낼 파라미터 준비
        Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("page", page);
        params.put("pageSize", pageSize);
        params.put("sort", sort);

        // 5. 현재 페이지에 해당하는 리뷰 목록 조회
        List<Review> reviewListFromDb = dao.selectReviewList(params);
        
        List<ReviewResponseDto> reviewResponseList = new ArrayList<>();

        // 6. (선택사항) 리뷰 이미지, 프로필 이미지 경로를 전체 URL로 변환
        for (Review review : reviewListFromDb) {
        	ReviewWriterDto userInfo = new ReviewWriterDto();
            userInfo.setUserNo(review.getUserNo());
            userInfo.setUsername(review.getUsername());
            userInfo.setSikBti(review.getSikBti());
            
            // userProfileImage가 null이 아닐 때만 전체 URL로 변환
            if (review.getUserProfileImage() != null && !review.getUserProfileImage().isEmpty()) {
                userInfo.setProfileImage(createFullUrl(review.getUserProfileImage()));
            }
            // 4-2. 리뷰 자체 이미지 경로 변환
            ReviewResponseDto reviewDto = new ReviewResponseDto();
            reviewDto.setReviewNo(review.getReviewNo());
            reviewDto.setUserInfo(userInfo);
            reviewDto.setStars(review.getStars());
            reviewDto.setContent(review.getContent());
            reviewDto.setReviewDate(review.getReviewDate());
            // imageUrl이 null이 아닐 때만 전체 URL로 변환
            if (review.getServerName() != null && !review.getServerName().isEmpty()) {
	            reviewDto.setServerName(createFullUrl(review.getServerName()));
	        }
            
            reviewResponseList.add(reviewDto);
        }
        
        // 7. 최종 결과를 DTO에 담아 반환
        return new ReviewPageResponse(reviewResponseList, totalPages);
	}

	@Override
	public List<ReviewResponseDto> selectPhotoReviewList(int rcpNo) {
		
		List<Review> photoReviewsFromDb  = dao.selectPhotoReviewList(rcpNo);
		
		List<ReviewResponseDto> photoReviewResponseList = new ArrayList<>();
		
        // 이미지 URL 변환 로직
		 for (Review review : photoReviewsFromDb) {
	            ReviewWriterDto userInfo = new ReviewWriterDto();
	            userInfo.setUserNo(review.getUserNo());
	            userInfo.setUsername(review.getUsername());
	            userInfo.setSikBti(review.getSikBti());
	            if (review.getUserProfileImage() != null && !review.getUserProfileImage().isEmpty()) {
	                userInfo.setProfileImage(createFullUrl(review.getUserProfileImage()));
	            }

	            ReviewResponseDto reviewDto = new ReviewResponseDto();
	            reviewDto.setReviewNo(review.getReviewNo());
	            reviewDto.setUserInfo(userInfo);
	            reviewDto.setStars(review.getStars());
	            reviewDto.setContent(review.getContent());
	            reviewDto.setReviewDate(review.getReviewDate());
	            if (review.getServerName() != null && !review.getServerName().isEmpty()) {
	                reviewDto.setServerName(createFullUrl(review.getServerName()));
	            }
	            
	            photoReviewResponseList.add(reviewDto);
	        }

	        return photoReviewResponseList;
	}

	@Override
	public void deleteReview(int reviewNo, long userNo) {
		Map<String, Object> params = new HashMap<>();
        params.put("reviewNo", reviewNo);
        params.put("userNo", userNo);
        dao.updateReviewDeleteStatus(params);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
	public void updateRecipe(int rcpNo, long userNo, RecipeWriteRequest request) throws Exception {
		// [사전 조회] 기존 레시피 정보를 불러와서, 기존 이미지 번호를 확보합니다.
        Recipe originalRecipe = dao.selectRecipeByNo(rcpNo);
        if (originalRecipe == null) {
            throw new Exception("존재하지 않는 레시피입니다.");
        }
        // 사용자가 다른 사람의 글을 수정하지 못하도록 보안 체크 (선택사항이지만 권장)
        if (originalRecipe.getUserNo() != userNo) {
            throw new Exception("수정 권한이 없습니다.");
        }
        long mainImageNo = originalRecipe.getImageNo(); // 기본값은 기존 이미지 번호

        // [이미지 처리] 새 대표 이미지가 있는지 확인하고 처리합니다.
        MultipartFile mainImageFile = request.getMainImage();
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            // 새 이미지가 있다면, 저장하고 새로운 imageNo를 받습니다 (createRecipe와 동일).
            String webPath = "community/recipe/" + rcpNo;
            String changeName = utilService.getChangeName(mainImageFile, webPath);
            String serverNameForDb = webPath + "/" + changeName;
            
            Map<String, Object> imageParam = new HashMap<>();
            imageParam.put("originName", mainImageFile.getOriginalFilename());
            imageParam.put("serverName", serverNameForDb);
            
            utilService.insertImage(imageParam);
            mainImageNo = utilService.getImageNo(imageParam);
        }
        // 새 이미지가 없다면, 위에서 설정한 기존 이미지 번호(mainImageNo)가 그대로 사용됩니다.
        
        // [영양소 계산] 수정된 재료 기준으로 영양소를 새로 계산하고 DB에 INSERT합니다 (createRecipe와 동일).
        Nutrient totalNutrient = calculateTotalNutrients(request.getIngredients());
        dao.insertNutrient(totalNutrient);

        // [레시피 객체 준비] 업데이트할 내용을 Recipe 객체에 담습니다.
        Recipe recipeToUpdate = new Recipe();
        recipeToUpdate.setRcpNo(rcpNo);
        recipeToUpdate.setUserNo(userNo);
        recipeToUpdate.setRcpName(request.getRcpName());
        recipeToUpdate.setRcpInfo(request.getRcpInfo());
        recipeToUpdate.setTag(request.getTag());
        recipeToUpdate.setRcpMthNo(request.getRcpMthNo());
        recipeToUpdate.setRcpStaNo(request.getRcpStaNo());
        recipeToUpdate.setImageNo((int)mainImageNo); // 새로 업데이트된 이미지 번호
        recipeToUpdate.setNutrientNo(totalNutrient.getNutrientNo()); 

        // [업데이트] RECIPE 테이블의 내용을 업데이트합니다.
        dao.updateRecipe(recipeToUpdate);

        // [기존 정보 삭제] 기존 재료 및 요리 순서 정보를 "전부 삭제"합니다.
        dao.deleteRcpIngredients(rcpNo);
        dao.deleteRcpDetails(rcpNo);

        // [신규 정보 삽입] 수정된 새로운 재료 및 요리 순서 정보를 "다시 삽입"합니다 (createRecipe와 동일).
        // 재료 정보 저장
        List<IngredientJsonDto> ingredients = objectMapper.readValue(request.getIngredients(), new TypeReference<>() {});
        for (IngredientJsonDto dto : ingredients) {
            RcpIngredient ing = new RcpIngredient();
            ing.setRcpNo(rcpNo);
            ing.setIngNo(dto.getIngNo());
            ing.setQuantity(dto.getQuantity());
            ing.setWeight(dto.getWeight());
            dao.insertRcpIngredient(ing);
        }

        // 요리 순서 저장
        List<String> descriptions = request.getStepDescriptions();
        List<MultipartFile> images = request.getStepImages();
        for (int i = 0; i < descriptions.size(); i++) {
            RcpDetail detail = new RcpDetail();
            detail.setRcpNo(rcpNo);
            detail.setRcpOrder(i + 1);
            detail.setDescription(descriptions.get(i));
            
            if (images != null && i < images.size()) {
                MultipartFile stepImgFile = images.get(i);
                if (stepImgFile != null && !stepImgFile.isEmpty()) {
                    String webPath = "community/recipe/" + rcpNo;
                    String changeName = utilService.getChangeName(stepImgFile, webPath);
                    String serverNameForDb = webPath + "/" + changeName;
                    
                    Map<String, Object> imageParam = new HashMap<>();
                    imageParam.put("originName", stepImgFile.getOriginalFilename());
                    imageParam.put("serverName", serverNameForDb);
                    
                    utilService.insertImage(imageParam);
                    long stepImageNo = utilService.getImageNo(imageParam);
                    detail.setImageNo((int)stepImageNo);
                }
            }
            dao.insertRcpDetail(detail);
        }
    }

	@Override
	public void deleteRecipe(int rcpNo) {
        dao.updateRecipeDeleteStatus(rcpNo);		
	}

	@Override
    @Transactional
	public RecipeDetailResponse selectOfficialRecipeDetail(int rcpNo, Long userNo, boolean increaseViewCount) {
		if (increaseViewCount) {
            dao.increaseViewCount(rcpNo);
        }
        
        RecipeDetailResponse recipe = dao.selectOfficialRecipeDetail(rcpNo);
        
        if (recipe == null) return null;
        
        if (userNo  != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("rcpNo", rcpNo);
            params.put("userNo", userNo );
            recipe.setBookmarked(dao.checkBookmark(params) > 0);
        }
       
        RcpIngs officialIngredients = dao.selectRcpIngs(rcpNo);
        if (officialIngredients != null) {
            recipe.setRcpIngList(officialIngredients.getRcpIngList());
        }
        
        recipe.setIngredients(new ArrayList<>());
        recipe.setSteps(dao.selectStepsByRcpNo(rcpNo));
        
        // 대표 이미지
        if (recipe.getMainImage() != null && !recipe.getMainImage().isEmpty()) {
        	String officialImageUrl;
        	if(recipe.getWriter() != null) {
        		 officialImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
    					.path("/images/" + recipe.getMainImage()).toUriString();
        	} else {
        		 officialImageUrl = "http://www.foodsafetykorea.go.kr" + recipe.getMainImage();
        	}
            recipe.setMainImage(officialImageUrl);
        }

        //작성자 프로필 이미지
        if (recipe.getWriter() != null && recipe.getWriter().getProfileImage() != null && !recipe.getWriter().getProfileImage().isEmpty()) {
            recipe.getWriter().setProfileImage(createFullUrl(recipe.getWriter().getProfileImage()));
        }
        
        // 요리 순서(Steps) 이미지 
        if (recipe.getSteps() != null) {
            for (CookingStep step : recipe.getSteps()) {            	
                if (step.getServerName() != null && !step.getServerName().isEmpty()) {
                	String officialStepImageUrl;
                	if(recipe.getWriter() != null) {
                		officialStepImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            					.path("/images/" + recipe.getMainImage()).toUriString();
                	} else {
                		officialStepImageUrl = "http://www.foodsafetykorea.go.kr" + step.getServerName();                		
                	}
                    step.setServerName(officialStepImageUrl);
                }
            }
        }
        
        System.out.println(">> isOfficial: " + recipe.isOfficial());
        return recipe;
	}

	@Override
	public BookmarkResponse getBookmarkStatus(int rcpNo, long userNo) {
		Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("userNo", userNo);
        
        boolean isBookmarked = dao.checkBookmark(params) > 0;
        int bookmarkCount = dao.countBookmarks(rcpNo);
        
        return new UserRecipeDto.BookmarkResponse(isBookmarked, bookmarkCount);
	}
	
	@Override
	public UserRecipeDto.BookmarkResponse toggleBookmark(int rcpNo, long userNo) {
		Map<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("userNo", userNo);
        
        boolean isBookmarked;
        
        if (dao.checkBookmark(params) > 0) {
        	dao.deleteBookmark(params);
            isBookmarked = false;
        } else {
        	 dao.insertBookmark(params);
             isBookmarked = true;
        }
        
        
        int bookmarkCount = dao.countBookmarks(rcpNo);
        return new UserRecipeDto.BookmarkResponse(isBookmarked, bookmarkCount);
	}

	@Override
	public OfficialRecipePage selectOfficialRecipePage(HashMap<String, Object> params) {
		int pageSize = 12; // 페이지 당 게시글 수
        params.put("pageSize", pageSize);
        
        // 프론트에서 받은 재료 문자열을 리스트로 변환 (기존 로직과 동일)
        String ingredientsParam = (String) params.get("ingredients");
        if (ingredientsParam != null && !ingredientsParam.isEmpty()) {
            List<String> ingredientList = Arrays.asList(ingredientsParam.split(","));
            params.put("ingredientList", ingredientList);
        }
        
        // DAO 호출
        long totalElements = dao.selectOfficialRecipeCount(params);
        List<OfficialRecipeResponse> recipes = dao.selectOfficialRecipeList(params);
        
//        log.debug("DB에서 조회된 전체 레시피 개수: {}", totalElements);
//        log.debug("DAO로부터 변환된 레시피 리스트: {}", recipes);
//        log.debug("변환된 리스트의 크기: {}", recipes.size());
//        
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
       
        
        // 이미지 전체 URL 생성
        for (OfficialRecipeResponse recipe : recipes) {
        	String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/images/")
	                .path(recipe.getServerName())
	                .toUriString();
        	recipe.setServerName(imageUrl);
        }
        
        return new OfficialRecipePage(recipes, totalPages, totalElements);
	}

	@Override
	public List<OfficialRecipeResponse> selectOfficialRankingRecipes() {
		List<OfficialRecipeResponse> rankingList = dao.selectOfficialRankingRecipes();
        
        // 이미지 전체 URL 생성
        for (OfficialRecipeResponse recipe : rankingList) {
        	if(recipe.getServerName() != null && !recipe.getServerName().isEmpty()) {
        		recipe.setServerName(createFullUrl(recipe.getServerName()));
        	}
        }
        return rankingList;
	}



	
}

