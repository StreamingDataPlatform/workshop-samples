# Code Hub

The officical code hub for DellEMC App Dev team

## Development

### Running locally

After you've got Jekyll [installed](https://jekyllrb.com/docs/installation/), make sure to install **jekyll-paginate** by execute `gem install jekyll-paginate`. Then clone or download this repo, `cd` into the folder and run `jekyll serve`.

### Hosting on GitHub

1. Fork this repo and configure "Repository name" and "Source" on the settings page.
2. Edit the `_config.yaml` file accordingly and commit.

Your Jekyll blog should be up and running now.

### Contribution

#### Write a post
+ Put the following snippet at the beginning of the post and customerize accordingly.

```
---
layout: post
category: docs
tags: [jekyll, code, markdown]
subtitle: some subtitle
img: image.jpg
author:
    name: Author Name
    description: Author Introduction
    image: Author Portrait
css:
js:
---

Some excerpt to describe the post

<!--more-->
```

Tips:

+ ** layout **: Set to ***post*** unless you make another perfect layout.
+ ** category **: Choose an existing one or assign a new one and if you want the category to be categorized on the catalog page. Modify the ***JB.catalog*** value of ***_config.yml*** with the appropiate order.
+ ** tags **: Put as many as that are relevant. the tags will be shown as a new tag or +1 on the existing one on Tag page.
+ ** subtitle **: It will show beneath the title on this post
+ ** img **: Put the image in the path *** assets/heliumjk/images/ *** and have the name here(prepend the folder name if put the image in a subfolder). It will show as thumbnail on Catalog and All Blogs page, and the full version will show at the right of the title on this post.
+ ** author **: Fill in with the name, description and image(do the same way to img). It will shown on the Author panel of this post.
+ ** css & js **: Optional. If you have addtional CSS & JS file for this post, put the file name here. The CSS file goes to *** assets/heliumjk/css/ *** and the JS to  *** assets/heliumjk/js/ ***. 
+ ** excerpt **: Have some expert to describe the post above of ``<!--more-->``

Finish the rest of the content with markdown syntex. Put the post into the *** _post *** folder and consider to put in the subfolder with the same category name for best practice.

#### Some other info
+ Refer to [list of supported languages and lexers](https://github.com/rouge-ruby/rouge/wiki/List-of-supported-languages-and-lexers) that supports highlight in code snippets.
+ Posts with the category of *** Demos *** with show up on the Demos page

## Credits

- spectral: [github](https://github.com/arkadianriver/spectral) [demo](http://arkadianriver.github.io/spectral/)
- heliumjk: [github](https://github.com/heliumjk/heliumjk.github.io) [demo](https://heliumjk.github.io/)
- huxpro: [github](https://github.com/Huxpro/huxpro.github.io) [demo](http://huangxuan.me/)