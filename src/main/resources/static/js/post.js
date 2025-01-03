// 작업자 : 이하린

// HTML 문서가 완전히 로드될 때까지 기다린 후 코드를 실행
document.addEventListener("DOMContentLoaded", function () {
    let canvas;
    let canvasIsModify = document.getElementById("canvas").dataset.ismodify === "true"; // 수정용 여부 확인

    // modal1 : 게시글 작성 모달
    let modal1 = document.getElementById("modal1");
    let btn1 = document.querySelector(".new-think");
    let span1 = document.getElementById("close1");

    btn1.onclick = function () {
        modal1.style.display = "block";
    }

    span1.onclick = function () {
        modal1.style.display = "none";
    }

    // modal2 : 그림 그리기 모달
    let modal2 = document.getElementById("modal2");
    let btn2 = document.getElementsByClassName("btn2")[0];
    let span2 = document.getElementById("close2");

    btn2.onclick = function () {
        if (!canvas) {
            console.log("init canvas");
            canvasInit();
        }
        modal2.style.display = "block";
        canvasIsModify = false;
    }

    span2.onclick = function () {
        modal2.style.display = "none";
        canvas.dispose();
        canvas = null;
    }

    // "이미지 추가" 버튼 클릭 이벤트 리스너
    document.getElementById("add-image").addEventListener("click", () => {
        document.getElementById("filereader").click(); // 파일 선택기 열기
    });

    function canvasInit() {
        canvas = new fabric.Canvas("canvas", {
            width: 550,
            height: 550,
            isDrawingMode: true,
            backgroundColor: "white",
        });

        // 기본 그리기 브러시 너비와 색상을 검정으로 설정
        canvas.freeDrawingBrush.width = 5;
        canvas.freeDrawingBrush.color = "black";

        // 버튼 클릭 이벤트 리스너

        // 색상 선택기
        document.getElementById('color-picker').addEventListener('input', (e) => {
            canvas.freeDrawingBrush.color = e.target.value;
        });

        // 브러시 버튼 클릭 이벤트 리스너
        document.getElementById("brush").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = parseInt(document.getElementById('brush-size').value, 10);
            canvas.freeDrawingBrush.color = document.getElementById('color-picker').value; // 색상 선택기에서 현재 색상 가져오기
            canvas.isDrawingMode = true; // 그리기 모드 활성화
        });

        // 지우개 버튼 클릭 이벤트 리스너
        document.getElementById("erase").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = parseInt(document.getElementById('brush-size').value, 10);
            canvas.freeDrawingBrush.color = canvas.backgroundColor; // 캔버스 배경색으로 설정
            canvas.isDrawingMode = true; // 그리기 모드 활성화
        });

        // 브러시 크기 조절
        document.getElementById('brush-size').addEventListener('input', (e) => {
            canvas.freeDrawingBrush.width = parseInt(e.target.value, 10);
        });


        // "그림 첨부" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("attach").addEventListener("click", (e) => {
            e.preventDefault();
            // Canvas 내용을 데이터 URL로 변환
            let dataURL = canvas.toDataURL({format: 'png'});
            let imageDataInput;
            let attachedImagesContainer;
            let attachedImage = document.createElement('img');
            attachedImage.src = dataURL;
            attachedImage.style.maxWidth = '30%';
            attachedImage.style.height = '35%';

            // 고유한 ID 부여
            attachedImage.id = `temp-image-${Date.now()}`;

            if (!canvasIsModify) {
                imageDataInput = document.getElementById('imageData');
                attachedImagesContainer = document.getElementById('attachedImagesContainer');
            } else {
                imageDataInput = document.getElementById('postImageData'); // 수정 캔버스
                attachedImagesContainer = document.getElementById('postAttachedImagesContainer');
            }
            attachedImagesContainer.appendChild(attachedImage);

            let currentData = imageDataInput.value ? imageDataInput.value + "base64," + dataURL.split("base64,")[1] : dataURL;
            imageDataInput.value = currentData;

            canvas.dispose();
            canvas = null;
            document.getElementById('modal2').style.display = 'none';
        });

        // 파일 선택기 변경 이벤트 리스너
        document.getElementById("filereader").addEventListener("change", (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const img = new Image();
                    img.onload = function() {
                        const fabricImage = new fabric.Image(img, {
                            left: 170,
                            top: 180,
                            scaleX: 2,
                            scaleY: 2
                        });

                        canvas.add(fabricImage);
                        canvas.setActiveObject(fabricImage);
                        canvas.renderAll();

                        // 사이즈 조정용
                        fabricImage.setControlsVisibility({
                            mt: true, // middle top
                            mb: true, // middle bottom
                            ml: true, // middle left
                            mr: true, // middle right
                            bl: true, // bottom left
                            br: true, // bottom right
                            tr: true, // top right
                            tl: true  // top left
                        });

                        // 크기 조절하는 동안에는 브러쉬모드 off
                        canvas.isDrawingMode = false;

                        // 새로 추가된 이미지의 크기 조정
                        fabricImage.scaleToWidth(canvas.width * 0.3); // 30%
                        fabricImage.scaleToHeight(canvas.height * 0.35); // 35%
                        canvas.renderAll();
                    }
                    img.src = e.target.result;

                }
                reader.readAsDataURL(file);
                event.target.value = ''; // file 내용 초기화
            }
        });

        document.getElementById('color-picker').addEventListener('click', () => {
            canvas.selection = false; // 선택 모드 비활성화
            canvas.isDrawingMode = true;
            document.getElementById('color-picker').enabled = true;
        });

        // 요소 선택 모드 버튼 클릭
        document.getElementById('select-element').addEventListener('click', () => {
            canvas.selection = true; // 선택 모드 활성화
            canvas.isDrawingMode = false; // 드로잉 모드 비활성화
            document.getElementById('color-picker').disabled = false; // 선택 모드일 때 색상 선택기 활성화
        });

        // 삭제 버튼 클릭 시 선택된 요소 삭제
        document.getElementById("delete-canvas-element").addEventListener("click", () => {
            // 선택된 모든 개체 get
            const activeObjects = canvas.getActiveObjects();

            if (activeObjects.length > 0) {
                activeObjects.forEach(obj => {
                    canvas.remove(obj);
                });

                // 선택된 객체 모두 삭제한 뒤 렌더링
                canvas.discardActiveObject();
                canvas.renderAll();
            }
        });
    }


    // 게시물 수정 버튼 이벤트 리스너
    document.body.addEventListener('click', function (e) {
        if (e.target.classList.contains('modifybtn')) {
            e.preventDefault();
            let postId = e.target.getAttribute('data-post-id');
            let modal = document.querySelector("#postModalContainer");
            let modalImageContainer = document.querySelector(".post-image-list");
            let carousel = e.target.closest('.btn-group').parentElement.parentElement.nextElementSibling.querySelector(".carousel-inner"); // .btn-group 다음에 오는 .carousel slide
            let imageTags = carousel ? carousel.querySelectorAll('img') : []; // carousel이 없는 경우 빈 배열로 처리

            modalImageContainer.innerHTML = "";

            let postContent = document.querySelector("#post-container-" + postId + " .fw-bold.fs-5").getAttribute('data-content');
            let textarea = modal.querySelector("textarea");
            textarea.value = postContent;

            // 이미지가 존재할 경우에만 처리
            if (imageTags.length > 0) {
                for (let image of imageTags) {
                    const image_no = image.dataset.id;
                    const image_src = image.src;

                    let newImageTag = document.createElement("img");
                    newImageTag.src = image_src;
                    newImageTag.dataset.id = image_no;
                    newImageTag.classList = ["d-block", "w-10"];
                    newImageTag.id = `image-${image_no}`;
                    newImageTag.style.width = '30%';
                    newImageTag.style.height = '35%';
                    modalImageContainer.appendChild(newImageTag);
                }
            }

            let deleteBtn = document.getElementById("postDelBtn");
            let addBtn = document.getElementById("postAddBtn");
            let closeBtn = document.querySelector("#postModalClose");
            let submitBtn = document.querySelector("#postModBtn");
            let modForm = document.querySelector(".modForm"); // 수정된 폼 클래스 이름

            modal.style.display = "block";

            if (deleteBtn) {
                deleteBtn.onclick = function (e) {
                    e.preventDefault();
                    deleteBtn.style.display = 'none';

                    // 각 이미지에 체크박스 생성
                    let images = modalImageContainer.querySelectorAll("img");
                    let imageCheckboxes = [];
                    images.forEach(image => {
                        let imageId = image.dataset.id;
                        if (!document.getElementById(`checkbox-${imageId}`)) {
                            let checkbox = document.createElement("input");
                            checkbox.type = "checkbox";
                            checkbox.id = `checkbox-${imageId}`;
                            image.after(checkbox);
                            imageCheckboxes.push(checkbox);
                        }
                    });

                    let modalContent = modal.querySelector('.modal-content');
                    let confirmDeleteBtn = document.createElement("button");
                    confirmDeleteBtn.innerText = "선택 삭제";
                    confirmDeleteBtn.id = 'confirm-delete-btn';
                    confirmDeleteBtn.classList.add('btn', 'btn-primary', 'mt-1');
                    modalContent.appendChild(confirmDeleteBtn);

                    // 이미지 체크 박스 선택 후 삭제 확인 버튼 핸들러
                    confirmDeleteBtn.onclick = function () {
                        let selectedImageIds = [];
                        for (let check of imageCheckboxes) {
                            if (check.checked) {
                                // 'temp-image'로 시작하는 ID를 가진 이미지와 서버에 존재하는 이미지를 모두 처리
                                selectedImageIds.push(check.previousSibling.id);
                            }
                        }

                        if (selectedImageIds.length > 0) {
                            alert("바로 이미지가 삭제됩니다. 지우시겠어요?");
                            deleteSelectedImages(selectedImageIds);
                        } else {
                            alert("이미지가 선택되지 않았어요.");
                        }

                        // 버튼과 체크박스 숨기기
                        for (let check of imageCheckboxes) {
                            check.remove();
                        }
                        confirmDeleteBtn.style.display = 'none';
                        cancelBtn.style.display = 'none';
                        deleteBtn.style.display = 'block'; // 삭제 버튼을 다시 보이게 함
                    };

                    // 취소 버튼
                    let cancelBtn = document.createElement("button");
                    cancelBtn.type = "button";
                    cancelBtn.innerText = "선택 취소";
                    cancelBtn.classList.add('btn', 'btn-outline-dark', 'mt-1');
                    modalContent.appendChild(cancelBtn);
                    cancelBtn.addEventListener("click", function () {
                        // 체크박스와 버튼 숨기기
                        for (let check of imageCheckboxes) {
                            check.remove();
                        }
                        cancelBtn.style.display = 'none';
                        confirmDeleteBtn.style.display = 'none';
                        deleteBtn.style.display = 'block'; // 삭제 버튼을 다시 보이게 함
                    });
                };
            }

            // 게시글 수정 모달 내 이미지 삭제
            function deleteSelectedImages(imageIds) {
                let serverImageIds = [];
                const postAttachedImagesContainer = document.getElementById('postAttachedImagesContainer'); // 수정 모달 내 이미지 컨테이너
                const imageDataInput = document.getElementById('postImageData'); // 수정 캔버스

                // 현재 imageDataInput의 값
                let imageData = imageDataInput.value;

                // 선택된 이미지 ID에 따라 클라이언트와 서버에서의 처리
                imageIds.forEach(id => {
                    if (id.startsWith('temp')) {
                        // 'temp-image'로 시작하는 ID는 클라이언트에서 직접 제거
                        const tempImage = document.getElementById(id);
                        if (tempImage) {
                            tempImage.remove();

                            // postAttachedImagesContainer에서 제거
                            if (postAttachedImagesContainer) {
                                const containerImage = document.getElementById(id);
                                if (containerImage) containerImage.remove();
                            }

                            // base64 데이터 제거
                            const tempImageDataURL = tempImage.src;
                            const base64Data = tempImageDataURL.split("base64,")[1];

                            // base64 데이터를 정확히 찾아서 제거
                            if (base64Data) {
                                const base64Prefix = "base64," + base64Data;
                                imageData = imageData.replace(base64Prefix, '');

                                // 빈 문자열로 바뀐 경우 'base64,' 부분 제거
                                if (imageData.startsWith("base64,")) {
                                    imageData = imageData.substring("base64,".length);
                                }
                            }
                        }
                    } else if (id.startsWith('image')) {
                        // 서버에 존재하는 이미지는 서버 ID 배열에 추가
                        const actualImageId = id.split('-')[1]; // "image-55"에서 "55"를 추출
                        serverImageIds.push(Number(actualImageId)); // Number 함수로 숫자로 변환
                    }
                });

                // 업데이트된 imageData를 다시 입력 필드에 설정
                imageDataInput.value = imageData;

                // 서버에 존재하는 이미지가 있을 경우 삭제 요청
                if (serverImageIds.length > 0) {
                    fetch('/images/delete', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ selectedImageIds: serverImageIds })
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                // 서버에서 삭제 성공 시 클라이언트에서도 이미지 제거
                                serverImageIds.forEach(id => {
                                    const image = document.getElementById(`image-${id}`);
                                    if (image) image.remove();
                                });
                            } else {
                                alert("이미지 삭제 실패");
                            }
                        })
                        .catch(error => console.error('Error:', error));
                }
            }

            closeBtn.onclick = function () {
                modal.style.display = "none";
            }

            addBtn.onclick = function () {
                e.preventDefault();
                if (canvas == null) {
                    console.log("init canvas")
                    canvasInit();
                }
                modal2.style.display = "block";
                canvasIsModify = true;
                console.log("캔버스 체크" + canvas);
            }

            // 게시물 수정 완료 버튼을 눌렀을 때 제출할 form의 action attribute 추가 및 submit 처리
            submitBtn.onclick = function (e) {
                e.preventDefault();
                let baseURL = "/post/modify/" + postId;
                modForm.setAttribute("action", baseURL);
                modForm.submit();
            }
        }

        else if (e.target.classList.contains('reply-button')) {
            const cardFooter = e.target.parentElement.parentElement.parentElement.nextElementSibling;
            if (cardFooter.style.display == 'none') {
                cardFooter.style.display = 'flex';
            } else {
                cardFooter.style.display = 'none';
            }
        }
    });
});