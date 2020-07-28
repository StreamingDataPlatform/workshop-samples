# SDP Code Hub
The official code hub for Dell EMC Streaming Data Platform. This website hosts an essential collection of code
samples, demos, and connectors to kick-start your projects on Pravega streaming storage and Streaming Data Platform
products. The goal of the Code Hub site is to provide customers and users with enough information (between local and
linked content) to get started developing applications that use these two products. The site is extensible, and
supports posting about virtually any relevant public source project or website. Anyone can contribute to the posts,
so long as they meet the guidelines provided below and pass the review process.  

# Posting Process
The following sections describe the necessary process to add a post to the code hub site.

## Fork and PR
To add a post, you will need to fork this repository, and create a branch based on the head of the **gh-pages** branch.  When you are ready to submit your post, create a PR to this repository targeting the **gh-pages** branch.  The only addition that should be made in the PR is a single post file. 

## The Post File
To create a post, add a file under the `_posts` directory (in the appropriate category subdirectory) with the following format:
```
{YEAR}-{MONTH}-{DAY}-{title}.{MARKUP}
```
Where **YEAR** is a four-digit number, **MONTH** and **DAY** are both two-digit numbers, and **MARKUP** is the file extension representing the format used in the file (typically `.md` for Markdown).

## Source Project Requirements
The origin project represented in the posting must meet the following requirements:
* Project source must be public and links to the project and documentation should be included in the post
* The project must be completely implemented, documented, and work on the latest release of Pravega and/or SDP
* In addition to any content posted on the Code Hub, there must be a detailed readme or home page for the project that includes full instructions on how to configure and use it

## Post Requirements
All posts must meet the following requirements to be considered for publishing:
* The identity of the poster must be clear; make sure your github profile is accurate and include your name and role in the author section
* Must have a title and description that clearly explains the purpose and intent of the sample, demo, or connector
* Must be appropriate and relevant, and align to the purpose of the Code Hub site
* Each post must include a clear license statement, usage permissions, and any applicable copyright attributions
    * Posts must also adhere to any license stipulations required by **all** linked source projects 
* The instructions (which may include linked readmes or documentation) should be clear, and anyone should be able to run or use the sample without trouble or confusion

## Review Process
All posts will be reviewed by the SDP App Dev team, where we will do the following:
* Check the above requirements are met, and note any gaps as PR review comments
* Run the actual sample using the provided instructions; any issues encountered will also be added as review comments
* Once approved, the PR will be merged and the post will be immediately published and visible

**Note**: SDP Code Hub maintainers reserve the right to approve or deny posts for any reason.

# Post Details

## Header
Put the following snippet at the beginning of the post and customize accordingly:
```
---
layout: post
category: Getting Started
tags: [jekyll, code, markdown]
subtitle: some subtitle
technologies: [flink, pravega, etc] 
license: Apache/MIT, etc
support: Community/Commercial, etc
img: image.jpg
author:
    name: Author Name
    description: Author Introduction
    image: Author Portrait
css:
js:
---

some excerpt
<!--more-->
```
### Header Fields

+ **layout**: Set to ***post***.
+ **category**: Choose one of the following categories: ``Getting Started``, ``Ingesting Data``, ``Processing Data``, ``Data Output``, ``Demos``, ``Connectors``.
+ **tags**: Put as many as are relevant. Tags will be shown on the Tags page.
+ **subtitle**: This will show beneath the title on the post details page.
+ **technologies**: List what technologies(less or equal than 2) is used in this post. This will show on the right panel.
+ **license**: List what is the licence type of this post. This will show on the right panel.
+ **support**: List the support model of this post. This will show on the right panel.
+ **img**: Put the image in the path ``assets/heliumjk/images/`` and have the name here (prepend the folder name if put in a subfolder). It will show as thumbnail on the Catalog and All Posts pages, and the full version will show at the right of the title on the post details page.
+ **author**: Fill in with your name, description (role) and image (optional - same location as img). This is shown on the Author panel of the post details page.
+ **css & js**: Optional. If you have additional CSS & JS files for this post, put the file names here. The CSS file goes to ``assets/heliumjk/css/`` and the JS to ``assets/heliumjk/js/``. 
+ **excerpt**: Brief introuduction of the post. The "excerpt" will show between "title" and "read more" on the post card
## Content

The main content starts after the header, and supports markdown syntax.  Describe the details of the sample, its purpose, instructions, links to the source project, license info, etc.  There is no strict structure to these posts, but you can use the following as a template:
```
# Long title

## Purpose
State the point/purpose of the sample and what it accomplishes (illustrates X, connects to Y, etc.)

## Design/Details
Long description of the sample and any design info or details that the reader should know.

## Instructions
List and/or links of detailed instructions on how to configure and use/run the sample.

## Source
Link to the source code/project/website.

## Documentation
Link to the source/detailed documentation.

## License / Copyright
State the general usage license type, copyright notice, and link to the full license
```

## Additional info
* Posts with the category of **Demos** with show up on the Demos page, and **Connectors** will show up on the Connectors page.  The rest will appear in the appropriate section of the Code Samples page.
* Jekyll docs: https://jekyllrb.com/docs/posts/
* Refer to [list of supported languages and lexers](https://github.com/rouge-ruby/rouge/wiki/List-of-supported-languages-and-lexers) that supports highlight in code snippets.

# Testing locally

1. [Install Jekyll](https://jekyllrb.com/docs/installation/)
1. Install **jekyll-paginate** (`gem install jekyll-paginate`)
1. In your local forked **gh-pages** branch, run `jekyll serve`