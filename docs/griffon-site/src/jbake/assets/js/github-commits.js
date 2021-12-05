$(function () {
    var $commitsList = $('.commits-list');
    var keyValuePairs = window.location.href.slice(window.location.href.indexOf("?") + 1).split("&");
    var x, params = {};
    $.each(keyValuePairs, function (i, keyValue) {
        x = keyValue.split('=');
        params[x[0]] = x[1];
    });

    $('#repo-owner').text(params.owner);
    $('#repo-name').text(params.repo);

    if (params.width) {
        $('.widget-container').css('width', params.width + 'px');
    }
    if (params.height) {
        $('.widget-container').css('height', params.height + 'px');
        $('.widget-container .commits-list').css('height', (params.height - 31) + 'px');
    }
    if (params.heading) {
        $('.widget-container h1').text(decodeURIComponent(params.heading));
    }

    $('.widget-container .header').click(function () {
        window.open('https://github.com/' + params.owner + '/' + params.repo);
    });

    params.sha = params.sha || 'master';

    $.ajax("https://api.github.com/repos/" + params.owner + "/" + params.repo + "/commits?sha=" + params.sha + "&callback=callback", {
        dataType: 'jsonp',
        type: 'get',
        data: {
            per_page: params.limit || 10
        },
        success: function (response, textStatus, jqXHR) {
            var data = response.data;
            $.each(data, function (i, commit) {
                var $li = $('<li></li>').appendTo($commitsList);
                if (commit.sha && commit.commit && commit.commit.message && commit.commit.author && commit.commit.committer && commit.commit.committer.date) {
                    if (commit.author && commit.author.gravatar_id) {
                        $('<div class="left"><img src="https://www.gravatar.com/avatar/' + commit.author.gravatar_id + '.png?s=38&r=pg&d=identicon"></div>').appendTo($li);
                    } else {
                        $('<div class="left"><img src="https://www.gravatar.com/avatar/61024896f291303615bcd4f7a0dcfb74.png?s=38&r=pg&d=identicon"></div>').appendTo($li);
                    }
                    $right = $('<div class="right"></div>').appendTo($li);
                    $('<span class="commit-message"><a href="https://github.com/' + params.owner + '/' + params.repo + '/commit/' + commit.sha + '" target="_blank">' + commit.commit.message + '</a></span><br/>').appendTo($right);
                    $('<span class="commit-meta">by <span class="committer-name">' + commit.commit.author.name + '</span> - <span class="commit-time">' + $.timeago(commit.commit.committer.date) + '</span></span>').appendTo($right);
                    $('<div class="clearfix"></div>').appendTo($li);
                } else {
                    // Render nothing.  Or render a message:
                    // $('<div class="left">&nbsp;</div>').appendTo( $li );
                    // $right = $('<div class="right"></div>').appendTo( $li );
                    // $('<span class="commit-meta">this commit cannot be rendered</span>').appendTo( $right );
                    // $('<div class="clearfix"></div>').appendTo( $li );
                }
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // Do something!
        }
    });
});
