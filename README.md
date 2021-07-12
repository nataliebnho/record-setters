Original App Design Project - README 
===

# GUINESS WORLD RECORD APP... for commoners

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
The Guinness World Record franchise distributes thousands of coveted titles for human achievement and natural extremes. However, the barrier to secure a Guinness World Record is long, tedious, and can be deterring to those who simply believe there is someone out there who can do their skill better. Furthermore, while Guinness offers thousands of categories, the category must be approved and deemed broad/relevant enough so that people would be interested in viewing it. 

The Guinness World Record App for the Common People will enable users to post a video of themselves doing their special skill - making it as niche, complicated, or simple as they desire. Users can see what categories others have posted, and challenge the current leader by uploading a video of themselves completing the same skill. Users will be empowered to share their prized achievements and skills, and will be much easier and quicker for users to secure a crown title.       

### App Evaluation
- **Category:** Photo & Video / Social
- **Mobile:** This app would be primarily developed for mobile but would perhaps be just as viable on a computer, such as Tinder, Instagram, etc. or other similar apps. Functionality would not be limited to mobile devices, however the mobile version would probably have more features, like a camera to directly record your skill.
- **Story:** Allows users to share their skill and create a new category that any user can challenge. For each skill, there will be a public leaderboard. 
- **Market:** Any individual can use and enjoy this app. People all around the world and of all ages can have their own unique skill they wish to share. Furthermore, people enjoy watching cool achievements. Even if users don't have a skill they want to share/compete, they could simply be a watcher who appreciate other people's talents.  
- **Habit:**  Individuals can use this app as frequently or infrequently as they desire. While posting/challegning a skill encourages users to check whether someone has beaten them, any user can use this app however often they want. 
- **Scope:** Initially, this is a fundamentally a simple video and photo sharing app (similar to how Instagram began). However, this could later expand into a larger scope to support features like messaging other users. As the app expands in popularity, the app could even have a verifying team that checks the user videos for accuracy. A large scope could even have a connection to the worldwide vGuinness franchise, in which winners on this app could then go on to compete in the official Guinness competition.  

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users log in to access their previous skills, create new skills under their profile, and challenge other users skills. 
* User can create a new category and either upload a video/picture from their camera roll or take a video/picture directly in the app
* User can click on another user's post and upload a video/picture challenging that skill
* Each skill/category has a visible leaderboard
* Each user has badges for the skills that they have won
* Profile page for each user to see their skills/posts/challenges
* Search option for skills
* Liking and commenting on users posts

**Optional Nice-to-have Stories**

* Google Maps API on where winners are
* Messaging support
* Badges (How many skills the user has won)
* Following other users 
* User recommendations based on what they were interested in challenging others on

### 2. Screen Archetypes

* Login/Create account 
   * Upon Download/Reopening of the application, the user is prompted to log in to gain access to their profile information to be properly matched with another person.
   * Main home timeline screen of other users 
* Home timeline
   * Viewing other users skills and posts
* Create new skill
    * User can create their own categoy (if it doesn't already exist) and upload a video and description of their skill
* Challenge skill 
    * Will look similar to a create new skill screen, except users are challenging other user's post instead of creating their own
* User profile screens
    * Viewing own profile where user can see their own skills, challenges, and wins to other users
    * Viewing other user's profiles to see their skills, challenges, and wins


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Timeline
* Create new skill
* Profile

**Flow Navigation** (Screen to Screen)

* Forced log-in --> Account creation if user does not have an account
* Home timeline
   * Can jump to Create new skill screen with the "challenge" button
   * Can jump to viewing other user's profile by tapping on username
* Profile
    * Text fields for bio, edit name, etc.
    * Grid view of all past posts (new skills + challenges) 
* Create new skill
    * Will jump back to Home timeline to see new post
* Challenge an existing skill
    * Will jump back to Home timeline to see new post

## Wireframes
![SPLASH SCREEN-TITLE PAGE](https://user-images.githubusercontent.com/59462509/124803263-e2196b00-df26-11eb-86a6-6fe01c588826.png)
![LOG IN- SIGN UP](https://user-images.githubusercontent.com/59462509/124803246-dd54b700-df26-11eb-8302-2fe47a1de296.png)
![SIGN UP](https://user-images.githubusercontent.com/59462509/124802636-2eb07680-df26-11eb-9c2d-749d65cc3481.png)
![LOG IN](https://user-images.githubusercontent.com/59462509/124803252-df1e7a80-df26-11eb-8cf9-c002207e3cc4.png)
![HOME PAGE](https://user-images.githubusercontent.com/59462509/124803278-e6de1f00-df26-11eb-904a-3d2cacf590dd.png)
![PROFILE PAGE](https://user-images.githubusercontent.com/59462509/124803261-e04fa780-df26-11eb-9ea1-9fefbb4823fe.png)
![CREATE-UPLOAD PAGE](https://user-images.githubusercontent.com/59462509/124803287-e80f4c00-df26-11eb-8e27-1987210d33a0.png)
![CHALLENGE PAGE](https://user-images.githubusercontent.com/59462509/124803289-e9d90f80-df26-11eb-891e-ec9443fa2dfc.png)
![VIDEO DETAILS PAGE](https://user-images.githubusercontent.com/59462509/124803268-e34a9800-df26-11eb-943a-145f6fa318d0.png)
<img width="530" alt="Screen Shot 2021-07-07 at 1 13 30 PM" src="https://user-images.githubusercontent.com/59462509/124803272-e47bc500-df26-11eb-8dd3-e0391fcf7bc8.png">

## Schema 
### Models

Post
|    Property   |       Type      |                   Description                   |
|:-------------:|:---------------:|:-----------------------------------------------:|
| objectId      | String          | unique id for each user post (default field)    |
| author        | Pointer to User | post author                                     |
| video         | File            | video file that user posts                      |
| caption       | String          | video caption by author                         |
| category      | String          | category the post is competing in               |
| winner        | Boolean         | whether this post is the winner of the category |
| likesCount    | Number          | number of likes for the post                    |
| commentsCount | Number          | number of comments for the post                 |
| votesCount    | Number          | number of votes for the post                    |
| createdAt     | DateTime        | date when post was created (default field)      |

User
| Property |          Type          |               Description               |
|:--------:|:----------------------:|:---------------------------------------:|
| objectId | String                 | unique id for each user (default field) |
| username | Pointer to User        | chosen username                         |
| password | String                 | chosen password                         |
| email    | String                 | inputted email                          |
| badges   | Array of Badge objects | category wins                           |

Badge
| Property |          Type          |               Description               |
|:--------:|:----------------------:|:---------------------------------------:|
| objectId | String                 | unique id for each user (default field) |
| winner   | Pointer to User        | which user won this badge               |
| category | String                 | which category this badge is for        |


### Networking

* Home Feed Screen
  * (Read/GET) Query all posts where user is following
  * (Create/POST) Create a new like on a post
  * (Delete) Delete existing like
  * (Create/POST) Create a new comment on a post
  * (Delete) Delete existing comment
* Create Post Screen
  * (Create/POST) Create a new post object
* Challenge Post Screen 
  * (Create/POST) Create a new post object under an existing category  
* Profile Screen
  * (Read/GET) Query logged in user object
  * (Read/GET) Query all user posts
  * (Read/GET) Query all user badges/wins
  * (Update/PUT) Update user profile image
