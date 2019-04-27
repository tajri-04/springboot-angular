package com.tajri.jwtApp.repository.notes;

import com.tajri.jwtApp.model.notes.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

}
