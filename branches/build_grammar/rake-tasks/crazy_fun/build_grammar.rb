
# line 1 "build_grammar.rl"

class BuildFile
  
# line 67 "build_grammar.rl"

      
   def parse(data)
     
# line 12 "build_grammar.rb"
class << self
	attr_accessor :_build_grammar_actions
	private :_build_grammar_actions, :_build_grammar_actions=
end
self._build_grammar_actions = [
	0, 1, 0, 1, 1, 1, 2, 1, 
	3, 1, 4, 1, 7, 1, 9, 1, 
	10, 2, 3, 4, 2, 5, 9, 2, 
	10, 7, 3, 6, 8, 9, 4, 10, 
	6, 8, 9
]

class << self
	attr_accessor :_build_grammar_key_offsets
	private :_build_grammar_key_offsets, :_build_grammar_key_offsets=
end
self._build_grammar_key_offsets = [
	0, 0, 6, 8, 14, 18, 22, 24, 
	35, 39, 40, 46, 47, 52, 54, 56, 
	68, 74, 75, 80, 85, 87, 99, 104, 
	105, 109, 110, 115, 116, 121, 126, 131, 
	133, 145, 147, 158, 164
]

class << self
	attr_accessor :_build_grammar_trans_keys
	private :_build_grammar_trans_keys, :_build_grammar_trans_keys=
end
self._build_grammar_trans_keys = [
	32, 35, 9, 13, 97, 122, 10, 13, 
	40, 95, 48, 57, 97, 122, 32, 58, 
	9, 13, 32, 58, 9, 13, 97, 122, 
	32, 61, 95, 9, 13, 48, 57, 65, 
	90, 97, 122, 32, 61, 9, 13, 62, 
	32, 34, 58, 91, 9, 13, 34, 32, 
	41, 44, 9, 13, 10, 13, 97, 122, 
	32, 41, 44, 95, 9, 13, 48, 57, 
	65, 90, 97, 122, 32, 34, 58, 123, 
	9, 13, 34, 32, 44, 93, 9, 13, 
	32, 41, 44, 9, 13, 97, 122, 32, 
	44, 93, 95, 9, 13, 48, 57, 65, 
	90, 97, 122, 32, 34, 58, 9, 13, 
	34, 32, 61, 9, 13, 62, 32, 34, 
	58, 9, 13, 34, 32, 44, 125, 9, 
	13, 32, 44, 125, 9, 13, 32, 44, 
	93, 9, 13, 97, 122, 32, 44, 95, 
	125, 9, 13, 48, 57, 65, 90, 97, 
	122, 97, 122, 32, 61, 95, 9, 13, 
	48, 57, 65, 90, 97, 122, 32, 35, 
	9, 13, 97, 122, 32, 35, 9, 13, 
	97, 122, 0
]

class << self
	attr_accessor :_build_grammar_single_lengths
	private :_build_grammar_single_lengths, :_build_grammar_single_lengths=
end
self._build_grammar_single_lengths = [
	0, 2, 2, 2, 2, 2, 0, 3, 
	2, 1, 4, 1, 3, 2, 0, 4, 
	4, 1, 3, 3, 0, 4, 3, 1, 
	2, 1, 3, 1, 3, 3, 3, 0, 
	4, 0, 3, 2, 2
]

class << self
	attr_accessor :_build_grammar_range_lengths
	private :_build_grammar_range_lengths, :_build_grammar_range_lengths=
end
self._build_grammar_range_lengths = [
	0, 2, 0, 2, 1, 1, 1, 4, 
	1, 0, 1, 0, 1, 0, 1, 4, 
	1, 0, 1, 1, 1, 4, 1, 0, 
	1, 0, 1, 0, 1, 1, 1, 1, 
	4, 1, 4, 2, 2
]

class << self
	attr_accessor :_build_grammar_index_offsets
	private :_build_grammar_index_offsets, :_build_grammar_index_offsets=
end
self._build_grammar_index_offsets = [
	0, 0, 5, 8, 13, 17, 21, 23, 
	31, 35, 37, 43, 45, 50, 53, 55, 
	64, 70, 72, 77, 82, 84, 93, 98, 
	100, 104, 106, 111, 113, 118, 123, 128, 
	130, 139, 141, 149, 154
]

class << self
	attr_accessor :_build_grammar_indicies
	private :_build_grammar_indicies, :_build_grammar_indicies=
end
self._build_grammar_indicies = [
	0, 2, 0, 3, 1, 0, 0, 2, 
	4, 5, 5, 5, 1, 6, 7, 6, 
	1, 8, 9, 8, 1, 10, 1, 11, 
	13, 12, 11, 12, 12, 12, 1, 14, 
	15, 14, 1, 16, 1, 16, 17, 18, 
	19, 16, 1, 21, 20, 22, 23, 4, 
	22, 1, 25, 25, 24, 26, 1, 21, 
	23, 4, 27, 21, 27, 27, 27, 1, 
	28, 29, 30, 31, 28, 1, 33, 32, 
	34, 28, 35, 34, 1, 21, 23, 4, 
	21, 1, 36, 1, 33, 37, 39, 38, 
	33, 38, 38, 38, 1, 40, 41, 42, 
	40, 1, 44, 43, 45, 46, 45, 1, 
	47, 1, 47, 48, 49, 47, 1, 51, 
	50, 52, 53, 54, 52, 1, 55, 40, 
	56, 55, 1, 33, 37, 39, 33, 1, 
	57, 1, 52, 53, 58, 54, 52, 58, 
	58, 58, 1, 59, 1, 44, 61, 60, 
	44, 60, 60, 60, 1, 62, 63, 62, 
	64, 1, 25, 24, 25, 3, 1, 0
]

class << self
	attr_accessor :_build_grammar_trans_targs
	private :_build_grammar_trans_targs, :_build_grammar_trans_targs=
end
self._build_grammar_trans_targs = [
	1, 0, 2, 3, 4, 3, 5, 6, 
	5, 6, 7, 8, 7, 9, 8, 9, 
	10, 11, 14, 16, 11, 12, 12, 35, 
	13, 36, 15, 15, 16, 17, 20, 22, 
	17, 18, 18, 19, 21, 16, 21, 19, 
	22, 23, 33, 23, 24, 24, 25, 26, 
	27, 31, 27, 28, 29, 22, 30, 29, 
	30, 32, 32, 34, 34, 25, 36, 13, 
	3
]

class << self
	attr_accessor :_build_grammar_trans_actions
	private :_build_grammar_trans_actions, :_build_grammar_trans_actions=
end
self._build_grammar_trans_actions = [
	0, 0, 0, 26, 15, 13, 1, 1, 
	0, 0, 20, 15, 13, 15, 0, 0, 
	0, 9, 0, 3, 13, 15, 0, 23, 
	0, 11, 20, 13, 0, 9, 0, 5, 
	13, 15, 0, 0, 20, 15, 13, 15, 
	0, 17, 7, 13, 15, 0, 0, 0, 
	9, 0, 13, 15, 15, 15, 15, 0, 
	0, 20, 13, 20, 13, 15, 23, 15, 
	30
]

class << self
	attr_accessor :_build_grammar_eof_actions
	private :_build_grammar_eof_actions, :_build_grammar_eof_actions=
end
self._build_grammar_eof_actions = [
	0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 15, 0
]

class << self
	attr_accessor :build_grammar_start
end
self.build_grammar_start = 1;
class << self
	attr_accessor :build_grammar_first_final
end
self.build_grammar_first_final = 35;
class << self
	attr_accessor :build_grammar_error
end
self.build_grammar_error = 0;

class << self
	attr_accessor :build_grammar_en_main
end
self.build_grammar_en_main = 1;


# line 71 "build_grammar.rl"

     @data = data
     @data = @data.unpack("c*") if @data.is_a?(String)

     
# line 198 "build_grammar.rb"
begin
	 @p ||= 0
	pe ||=  @data.length
	cs = build_grammar_start
end

# line 76 "build_grammar.rl"

     begin
       
# line 209 "build_grammar.rb"
begin
	_klen, _trans, _keys, _acts, _nacts = nil
	_goto_level = 0
	_resume = 10
	_eof_trans = 15
	_again = 20
	_test_eof = 30
	_out = 40
	while true
	_trigger_goto = false
	if _goto_level <= 0
	if  @p == pe
		_goto_level = _test_eof
		next
	end
	if cs == 0
		_goto_level = _out
		next
	end
	end
	if _goto_level <= _resume
	_keys = _build_grammar_key_offsets[cs]
	_trans = _build_grammar_index_offsets[cs]
	_klen = _build_grammar_single_lengths[cs]
	_break_match = false
	
	begin
	  if _klen > 0
	     _lower = _keys
	     _upper = _keys + _klen - 1

	     loop do
	        break if _upper < _lower
	        _mid = _lower + ( (_upper - _lower) >> 1 )

	        if  @data[ @p] < _build_grammar_trans_keys[_mid]
	           _upper = _mid - 1
	        elsif  @data[ @p] > _build_grammar_trans_keys[_mid]
	           _lower = _mid + 1
	        else
	           _trans += (_mid - _keys)
	           _break_match = true
	           break
	        end
	     end # loop
	     break if _break_match
	     _keys += _klen
	     _trans += _klen
	  end
	  _klen = _build_grammar_range_lengths[cs]
	  if _klen > 0
	     _lower = _keys
	     _upper = _keys + (_klen << 1) - 2
	     loop do
	        break if _upper < _lower
	        _mid = _lower + (((_upper-_lower) >> 1) & ~1)
	        if  @data[ @p] < _build_grammar_trans_keys[_mid]
	          _upper = _mid - 2
	        elsif  @data[ @p] > _build_grammar_trans_keys[_mid+1]
	          _lower = _mid + 2
	        else
	          _trans += ((_mid - _keys) >> 1)
	          _break_match = true
	          break
	        end
	     end # loop
	     break if _break_match
	     _trans += _klen
	  end
	end while false
	_trans = _build_grammar_indicies[_trans]
	cs = _build_grammar_trans_targs[_trans]
	if _build_grammar_trans_actions[_trans] != 0
		_acts = _build_grammar_trans_actions[_trans]
		_nacts = _build_grammar_actions[_acts]
		_acts += 1
		while _nacts > 0
			_nacts -= 1
			_acts += 1
			case _build_grammar_actions[_acts - 1]
when 0 then
# line 10 "build_grammar.rl"
		begin
 
       # clear the stack
       while !@lhs[-1].is_a? OutputType
         leave
       end

       puts "Starting arg" if @debug
       @lhs.push ArgType.new
     		end
when 1 then
# line 19 "build_grammar.rl"
		begin

       puts "Starting array" if @debug
       @lhs.push ArrayType.new 
     		end
when 2 then
# line 23 "build_grammar.rl"
		begin
 
       puts "Starting map" if @debug
       @lhs.push MapType.new 
     		end
when 3 then
# line 27 "build_grammar.rl"
		begin
 
       puts "Starting map entry" if @debug
       @lhs.push MapEntry.new 
     		end
when 4 then
# line 31 "build_grammar.rl"
		begin
 
       puts "Starting string" if @debug
       @lhs.push StringType.new 
     		end
when 5 then
# line 35 "build_grammar.rl"
		begin
 
       puts "Starting symbol" if @debug
       @lhs.push SymbolType.new 
     		end
when 6 then
# line 39 "build_grammar.rl"
		begin
 
       puts "Starting type" if @debug
       # Unwind the stack until the top is another OutputType (or it's empty)
       while (!@lhs.empty?)
         puts "Unwinding [#{@lhs}]" + @lhs.length.to_s
         leave
       end
       
       @lhs.push OutputType.new 
     		end
when 7 then
# line 50 "build_grammar.rl"
		begin

       while (!@lhs.empty?)
          leave
        end
     		end
when 8 then
# line 56 "build_grammar.rl"
		begin
 
       puts "Starting type name" if @debug
       @lhs.push NameType.new 
     		end
when 9 then
# line 61 "build_grammar.rl"
		begin
 @lhs[-1] << @data[@p].chr 		end
when 10 then
# line 62 "build_grammar.rl"
		begin

       leave
     		end
# line 375 "build_grammar.rb"
			end # action switch
		end
	end
	if _trigger_goto
		next
	end
	end
	if _goto_level <= _again
	if cs == 0
		_goto_level = _out
		next
	end
	 @p += 1
	if  @p != pe
		_goto_level = _resume
		next
	end
	end
	if _goto_level <= _test_eof
	if  @p ==  @eof
	__acts = _build_grammar_eof_actions[cs]
	__nacts =  _build_grammar_actions[__acts]
	__acts += 1
	while __nacts > 0
		__nacts -= 1
		__acts += 1
		case _build_grammar_actions[__acts - 1]
when 10 then
# line 62 "build_grammar.rl"
		begin

       leave
     		end
# line 409 "build_grammar.rb"
		end # eof action switch
	end
	if _trigger_goto
		next
	end
end
	end
	if _goto_level <= _out
		break
	end
	end
	end

# line 79 "build_grammar.rl"
     rescue
       puts show_bad_line       
       throw $!
     end

    if cs == build_grammar_error
      throw show_bad_line
    end

    @types
  end
end
