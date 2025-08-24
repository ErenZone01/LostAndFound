export interface ChatMessage {
  type : string | null;
  content: string | null;
  sender: string | null;
  receiver: string | null;
  matchId : string | null;
  postId : string | null
}
