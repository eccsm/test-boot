  function showOrHideGrade(gradeType) {
        if (gradeType === "math") {
              var x = document.getElementById("mathGrade");
              if (x.style.display === "none") {
                x.style.display = "block";
              } else {
                x.style.display = "none";
              }
        }
        if (gradeType === "science") {
             var x = document.getElementById("scienceGrade");
             if (x.style.display === "none") {
                 x.style.display = "block";
            } else {
                x.style.display = "none";
            }
          }
        if (gradeType === "history") {
             var x = document.getElementById("historyGrade");
             if (x.style.display === "none") {
                 x.style.display = "block";
            } else {
                x.style.display = "none";
            }
          }
    }

    function deleteStudent(id) {
    window.location.href = "/delete/student/" + id;
    }

    function deleteMathGrade(id) {
    window.location.href = "/grade/" + id;
    }

    function deleteScienceGrade(id) {
    window.location.href = "/grade/" + id;
    }

    function deleteHistoryGrade(id) {
    window.location.href = "/grade/" + id;
    }

    function studentInfo(id) {
        window.location.href = "/studentInformation/" + id;
    }