var refreshRate = 2000; //mili seconds

function appendToChatArea(entries) {
    $("#chatarea").empty();
    $.each(entries || [], appendChatEntry);
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({scrollTop: height}, "slow");
}

function appendChatEntry(index, entry) {
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry(entry) {
    entry.chatString = entry.chatString.replace(":)", "<span class='smiley'></span>");
    return $("<span class=\"success\">").append(entry.username + ": " + entry.chatString);
}

function ajaxChatContent() {
    $.ajax({
        url: "../../gameactions/getchat",
        dataType: 'json',
        async: false,
        success: function (data) {
            appendToChatArea(data.entries);
            triggerAjaxChatContent();
        },
        error: function (error) {
            triggerAjaxChatContent();
        }
    });
}


$(function () { // o
    $("#chatform").submit(function () {
        $.ajax({
            data: $(this).serialize(),
            url: "../../gameactions/sendchat",
            timeout: 2000,
            success: function (r) {
                $("#userString").val('');
            }
        });

        $("#userstring").val("");
        return false;
    });
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}

$(function () {
    $.ajaxSetup({cache: false});
    triggerAjaxChatContent();
});

function updateUsersList(users) {
    $("#userslist").empty();

    $.each(users || [], function (index, username) {
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}

$(function ajaxUsersList() {
    $.ajax({
        url: "../../gameactions/userslist",
        error: function (error) {
            setTimeout(ajaxUsersList, refreshRate);
        },
        success: function (users) {
            updateUsersList(users);
        }
    });
});
