export interface Post {
    id: string;
    title: string;
    content: string;
    create_at: string;
    last_modify_at: string;
    read: boolean;
    user_id: string;
    category_id: string;
    comment: Comment[];
}

interface Comment {
    id: string;
}