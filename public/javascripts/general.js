$(document).ready(function () {
    $("a.mobile").click(function () {
        $(".sidebar").slideToggle('fast');
    });
    $(".commandboxbutton").click(function (event) {
        console.log("Clicked " + event.target.id)
        var commandBox = $("#commandbox");
        commandBox.find(".commandform").attr("action","/computer/sendcommand/" + event.target.id);
        commandBox.find(".modal-body #shutdown").attr("href","/computer/shutdown/" + event.target.id);
        commandBox.find(".modal-body #upgrade").attr("href","/computer/upgrade/" + event.target.id);
        commandBox.find(".modal-body #unfreeze").attr("href","/computer/unfreeze/" + event.target.id);
        commandBox.modal('show');
    })
});