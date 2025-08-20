import { State } from "./State"

export interface MatchReponse {
    id: string,
    userId: string,
    keyword: string,
    type: string,
    lieu: string,
    date: string,
    description: string[],
    category: string
    matchedPostIds: string[],
    active: string,
    createdAt: string,
    lastMatchDate: string,
    etat : State[]
}
