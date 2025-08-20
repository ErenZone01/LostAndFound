export interface PostResponse {
    postId : string | null | undefined,
    userId : string | null | undefined,
    type : string | null | undefined,
    lieu : string | null | undefined,
    date : string | null | undefined,
    imageId : string | null | undefined,
    imageBase64 : string | null | undefined,
    description : string[] ,
    category : string | null | undefined,
    active : string | null | undefined,
}

//<img [src]="imageBase64" alt="Post image">