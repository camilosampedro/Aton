$(document).ready(function () {
    $("a.mobile").click(function () {
        $(".sidebar").slideToggle('fast');
    });
    $(".commandboxbutton").click(function (event) {
        console.log("Clicked " + event.target.id)
        $("#commandbox .modal-body #shutdown").attr("href","/computer/shutdown/" + event.target.id)
        $("#commandbox .modal-body #upgrade").attr("href","/computer/upgrade/" + event.target.id)
        $("#commandbox .modal-body #unfreeze").attr("href","/computer/unfreeze/" + event.target.id)
        $('#commandbox').modal('show');
    })
});